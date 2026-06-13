package io.nekohasekai.sfa.utils

import android.util.Log
import go.Seq
import io.nekohasekai.libbox.CommandClientHandler
import io.nekohasekai.libbox.CommandClientOptions
import io.nekohasekai.libbox.ConnectionEvents
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.LogEntry
import io.nekohasekai.libbox.LogIterator
import io.nekohasekai.libbox.OutboundGroup
import io.nekohasekai.libbox.OutboundGroupItemIterator
import io.nekohasekai.libbox.OutboundGroupIterator
import io.nekohasekai.libbox.StatusMessage
import io.nekohasekai.libbox.StringIterator
import io.nekohasekai.sfa.ktx.toList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.nekohasekai.sfa.di.ApplicationScope
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CommandClient @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
) {
    private var localOnly: Boolean = false

    // Shared state for the singleton instance
    private val handlerTypes = Collections.synchronizedMap(mutableMapOf<Handler, Set<ConnectionType>>())
    private val activeHandlers = Collections.synchronizedSet(mutableSetOf<Any>())
    private var anonymousTypes = emptySet<ConnectionType>()
    private var currentRunningTypes = emptySet<ConnectionType>()
    private var isInternalReconnecting = false
    private var lastConnectedRemoteServerId: Long? = null

    // Secondary constructors for manual management or legacy/localOnly code
    constructor(
        scope: CoroutineScope,
        connectionTypes: List<ConnectionType>,
        handler: Handler,
        localOnly: Boolean = false
    ) : this(scope) {
        this.localOnly = localOnly
        this.addHandler(handler, connectionTypes.toSet())
    }

    constructor(
        scope: CoroutineScope,
        connectionType: ConnectionType,
        handler: Handler,
        localOnly: Boolean = false
    ) : this(scope, listOf(connectionType), handler, localOnly)

    private val additionalHandlers = Collections.synchronizedList(mutableListOf<Handler>())
    private var cachedGroups: MutableList<OutboundGroup>? = null
    private var cachedOutbounds: List<io.nekohasekai.libbox.OutboundGroupItem>? = null

    fun addHandler(handler: Handler, types: Set<ConnectionType>) {
        handlerTypes[handler] = types
        synchronized(additionalHandlers) {
            if (!additionalHandlers.contains(handler)) {
                additionalHandlers.add(handler)
                cachedGroups?.let { groups ->
                    handler.updateGroups(groups)
                }
                cachedOutbounds?.let { outbounds ->
                    handler.updateOutbounds(outbounds)
                }
            }
        }
        if (activeHandlers.contains(handler)) {
            syncConnection()
        }
    }

    fun addHandler(handler: Handler) {
        synchronized(additionalHandlers) {
            if (!additionalHandlers.contains(handler)) {
                additionalHandlers.add(handler)
                cachedGroups?.let { groups ->
                    handler.updateGroups(groups)
                }
                cachedOutbounds?.let { outbounds ->
                    handler.updateOutbounds(outbounds)
                }
            }
        }
    }

    fun removeHandler(handler: Handler) {
        handlerTypes.remove(handler)
        synchronized(additionalHandlers) {
            additionalHandlers.remove(handler)
        }
        if (activeHandlers.remove(handler)) {
            syncConnection()
        }
    }

    private fun getAllHandlers(): List<Handler> = synchronized(additionalHandlers) {
        additionalHandlers.toList()
    }

    enum class ConnectionType {
        Status,
        Groups,
        Log,
        ClashMode,
        Connections,
        Outbounds,
    }

    enum class ConnectionErrorKind {
        // A connect attempt failed; retrying is not expected to succeed.
        ConnectFailed,

        // An established connection dropped (app suspension, network change,
        // server restart); reconnecting may recover.
        ConnectionLost,
    }

    interface Handler {
        fun onConnected() {}
        fun onDisconnected() {}
        fun onConnectionError(kind: ConnectionErrorKind, message: String) {}
        fun updateStatus(status: StatusMessage) {}
        fun setDefaultLogLevel(level: Int) {}
        fun clearLogs() {}
        fun appendLogs(message: List<LogEntry>) {}
        fun updateGroups(newGroups: MutableList<OutboundGroup>) {}
        fun updateOutbounds(outbounds: List<io.nekohasekai.libbox.OutboundGroupItem>) {}
        fun initializeClashMode(modeList: List<String>, currentMode: String) {}
        fun updateClashMode(newMode: String) {}
        fun writeConnectionEvents(events: ConnectionEvents) {}
    }

    private val access = Any()
    private var connectionEpoch = 0
    private var commandClient: io.nekohasekai.libbox.CommandClient? = null

    fun setTypes(types: List<ConnectionType>) {
        anonymousTypes = types.toSet()
        if (activeHandlers.contains(this) || activeHandlers.isEmpty()) {
            syncConnection()
        }
    }

    fun connect(requester: Any = this) {
        if (activeHandlers.add(requester)) {
            syncConnection()
        }
    }

    fun disconnect(requester: Any = this) {
        if (activeHandlers.remove(requester)) {
            syncConnection()
        }
    }

    // This is for local non-injected callers who want to call connect() directly
    fun connect() {
        connect(this)
    }

    // This is for local non-injected callers who want to call disconnect() directly
    fun disconnect() {
        disconnect(this)
    }

    private fun syncConnection() {
        if (activeHandlers.isEmpty()) {
            disconnectInternal()
            currentRunningTypes = emptySet()
            return
        }

        val neededTypes = mutableSetOf<ConnectionType>()
        activeHandlers.forEach { requester ->
            if (requester is Handler) {
                handlerTypes[requester]?.let { neededTypes.addAll(it) }
            }
        }
        if (activeHandlers.contains(this)) {
            neededTypes.addAll(anonymousTypes)
        }
        
        if (neededTypes.isEmpty()) {
            if (anonymousTypes.isNotEmpty()) {
                neededTypes.addAll(anonymousTypes)
            } else {
                neededTypes.add(ConnectionType.Status)
            }
        }

        val remoteServer = if (localOnly) null else CommandTarget.remoteServer
        val remoteServerId = remoteServer?.id

        if (commandClient == null || neededTypes != currentRunningTypes || remoteServerId != lastConnectedRemoteServerId) {
            connectInternal(neededTypes, remoteServer)
        }
    }

    private fun connectInternal(types: Set<ConnectionType>, remoteServer: io.nekohasekai.sfa.database.RemoteServer?) {
        val epoch: Int
        val previousClient: io.nekohasekai.libbox.CommandClient?
        synchronized(access) {
            epoch = ++connectionEpoch
            previousClient = commandClient
            commandClient = null
        }

        isInternalReconnecting = previousClient != null
        if (previousClient != null) {
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onDisconnected() }
            }
        }

        currentRunningTypes = types
        lastConnectedRemoteServerId = remoteServer?.id

        scope.launch(Dispatchers.IO) {
            previousClient?.apply {
                runCatching {
                    disconnect()
                }
            }

            val options = CommandClientOptions()
            types.forEach { connectionType ->
                val command =
                    when (connectionType) {
                        ConnectionType.Status -> Libbox.CommandStatus
                        ConnectionType.Groups -> Libbox.CommandGroup
                        ConnectionType.Log -> Libbox.CommandLog
                        ConnectionType.ClashMode -> Libbox.CommandClashMode
                        ConnectionType.Connections -> Libbox.CommandConnections
                        ConnectionType.Outbounds -> Libbox.CommandOutbounds
                    }
                options.addCommand(command)
            }
            options.statusInterval = 3 * 1000 * 1000 * 1000 // 3 seconds custom optimization

            val newClient: io.nekohasekai.libbox.CommandClient
            try {
                newClient = if (remoteServer != null) {
                    Libbox.newRemoteCommandClient(
                        ClientHandler(epoch),
                        options,
                        CommandTarget.libboxOptions(remoteServer)
                    )
                } else {
                    io.nekohasekai.libbox.CommandClient(ClientHandler(epoch), options)
                }
                newClient.connect()
            } catch (e: Exception) {
                Log.d("CommandClient", "connect failed", e)
                if (isActiveEpoch(epoch)) {
                    getAllHandlers().forEach {
                        it.onConnectionError(
                            ConnectionErrorKind.ConnectFailed,
                            e.message ?: e.toString()
                        )
                    }
                }
                return@launch
            }

            val stale = synchronized(access) {
                if (epoch != connectionEpoch) {
                    true
                } else {
                    commandClient = newClient
                    false
                }
            }

            if (stale) {
                runCatching {
                    newClient.disconnect()
                }
            }
            isInternalReconnecting = false
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun disconnectInternal() {
        val client: io.nekohasekai.libbox.CommandClient?
        synchronized(access) {
            connectionEpoch++
            client = commandClient
            commandClient = null
        }
        lastConnectedRemoteServerId = null
        if (client != null) {
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onDisconnected() }
            }
            GlobalScope.launch(Dispatchers.IO) {
                runCatching {
                    client.disconnect()
                }
            }
        }
    }

    private fun isActiveEpoch(epoch: Int): Boolean = synchronized(access) { epoch == connectionEpoch }

    private inner class ClientHandler(private val epoch: Int) : CommandClientHandler {
        override fun connected() {
            if (!isActiveEpoch(epoch)) return
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onConnected() }
            }
            Log.d("CommandClient", "connected")
        }

        override fun disconnected(message: String?) {
            if (!isActiveEpoch(epoch)) return
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onDisconnected() }
            }
            if (message != null) {
                getAllHandlers().forEach {
                    it.onConnectionError(ConnectionErrorKind.ConnectionLost, message)
                }
            }
            Log.d("CommandClient", "disconnected: $message")
        }

        override fun writeGroups(message: OutboundGroupIterator?) {
            if (message == null || !isActiveEpoch(epoch)) return
            val groups = mutableListOf<OutboundGroup>()
            while (message.hasNext()) {
                groups.add(message.next())
            }
            cachedGroups = groups
            getAllHandlers().forEach { it.updateGroups(groups) }
        }

        override fun writeOutbounds(message: OutboundGroupItemIterator?) {
            if (message == null || !isActiveEpoch(epoch)) return
            val outbounds = mutableListOf<io.nekohasekai.libbox.OutboundGroupItem>()
            while (message.hasNext()) {
                outbounds.add(message.next())
            }
            cachedOutbounds = outbounds
            getAllHandlers().forEach { it.updateOutbounds(outbounds) }
        }

        override fun setDefaultLogLevel(level: Int) {
            if (!isActiveEpoch(epoch)) return
            getAllHandlers().forEach { it.setDefaultLogLevel(level) }
        }

        override fun clearLogs() {
            if (!isActiveEpoch(epoch)) return
            getAllHandlers().forEach { it.clearLogs() }
        }

        override fun writeLogs(messageList: LogIterator?) {
            if (messageList == null || !isActiveEpoch(epoch)) return
            val logs = messageList.toList()
            getAllHandlers().forEach { it.appendLogs(logs) }
        }

        override fun writeStatus(message: StatusMessage) {
            if (!isActiveEpoch(epoch)) return
            getAllHandlers().forEach { it.updateStatus(message) }
        }

        override fun initializeClashMode(modeList: StringIterator, currentMode: String) {
            if (!isActiveEpoch(epoch)) return
            val modes = modeList.toList()
            getAllHandlers().forEach { it.initializeClashMode(modes, currentMode) }
        }

        override fun updateClashMode(newMode: String) {
            if (!isActiveEpoch(epoch)) return
            getAllHandlers().forEach { it.updateClashMode(newMode) }
        }

        override fun writeConnectionEvents(events: ConnectionEvents?) {
            if (events == null || !isActiveEpoch(epoch)) return
            getAllHandlers().forEach { it.writeConnectionEvents(events) }
        }
    }
}
