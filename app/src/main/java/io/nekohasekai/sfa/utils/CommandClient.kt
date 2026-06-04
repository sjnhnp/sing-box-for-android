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

import io.nekohasekai.sfa.di.ApplicationScope
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CommandClient @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
) {
    // Shared state for the singleton instance
    private val handlerTypes = Collections.synchronizedMap(mutableMapOf<Handler, Set<ConnectionType>>())
    private val activeHandlers = Collections.synchronizedSet(mutableSetOf<Any>())
    private var anonymousTypes = emptySet<ConnectionType>()
    private var currentRunningTypes = emptySet<ConnectionType>()
    private var isInternalReconnecting = false

    // Secondary constructor for manual management or legacy code
    constructor(scope: CoroutineScope, connectionType: ConnectionType, handler: Handler) : this(scope) {
        this.addHandler(handler, setOf(connectionType))
    }

    constructor(scope: CoroutineScope, connectionTypes: List<ConnectionType>, handler: Handler) : this(scope) {
        this.addHandler(handler, connectionTypes.toSet())
    }

    private val additionalHandlers = Collections.synchronizedList(mutableListOf<Handler>())
    private var cachedGroups: MutableList<OutboundGroup>? = null
    private var cachedOutbounds: List<io.nekohasekai.libbox.OutboundGroupItem>? = null

    /**
     * Set types for this client. 
     * If using the singleton instance, it's recommended to use addHandler(handler, types) instead.
     */
    fun setTypes(types: List<ConnectionType>) {
        anonymousTypes = types.toSet()
        if (activeHandlers.contains(this) || activeHandlers.isEmpty()) {
            // If already active or no handlers yet (legacy behavior), we'll use these types on next update
            syncConnection()
        }
    }

    /**
     * Add a handler with its required connection types.
     */
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

    /**
     * Add a handler using default types (legacy support).
     */
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

    interface Handler {
        fun onConnected() {}
        fun onDisconnected() {}
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

    private var commandClient: io.nekohasekai.libbox.CommandClient? = null
    private val clientHandler = ClientHandler()

    /**
     * Request a connection. If the socket is already open, it remains open.
     * If types need to be updated, it will reconnect.
     * @param requester The object requesting the connection (usually 'this' in ViewModels).
     */
    fun connect(requester: Any = this) {
        if (activeHandlers.add(requester)) {
            syncConnection()
        }
    }

    /**
     * Release a connection request. If this was the last requester, the socket is closed.
     * @param requester The object that previously requested the connection.
     */
    fun disconnect(requester: Any = this) {
        if (activeHandlers.remove(requester)) {
            syncConnection()
        }
    }

    private fun syncConnection() {
        if (activeHandlers.isEmpty()) {
            disconnectInternal()
            currentRunningTypes = emptySet()
            return
        }

        // Calculate union of all requested types
        val neededTypes = mutableSetOf<ConnectionType>()
        activeHandlers.forEach { requester ->
            if (requester is Handler) {
                handlerTypes[requester]?.let { neededTypes.addAll(it) }
            }
        }
        if (activeHandlers.contains(this)) {
            neededTypes.addAll(anonymousTypes)
        }
        
        // If no types specified yet but we want to connect, use what we have or just Status as default
        if (neededTypes.isEmpty()) {
            if (anonymousTypes.isNotEmpty()) {
                neededTypes.addAll(anonymousTypes)
            } else {
                neededTypes.add(ConnectionType.Status)
            }
        }

        if (commandClient == null || neededTypes != currentRunningTypes) {
            connectInternal(neededTypes)
        }
    }

    private fun connectInternal(types: Set<ConnectionType>) {
        val wasConnected = commandClient != null
        if (wasConnected) {
            isInternalReconnecting = true
            disconnectInternal()
        }

        currentRunningTypes = types
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
        options.statusInterval = 3 * 1000 * 1000 * 1000
        val commandClient = io.nekohasekai.libbox.CommandClient(clientHandler, options)
        try {
            commandClient.connect()
        } catch (e: Exception) {
            Log.d("CommandClient", "connect failed", e)
            return
        }
        this.commandClient = commandClient
        
        isInternalReconnecting = false
    }

    private fun disconnectInternal() {
        commandClient?.apply {
            runCatching {
                disconnect()
            }
        }
        commandClient = null
    }

    private inner class ClientHandler : CommandClientHandler {
        override fun connected() {
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onConnected() }
            }
            Log.d("CommandClient", "connected")
        }

        override fun disconnected(message: String?) {
            if (!isInternalReconnecting) {
                getAllHandlers().forEach { it.onDisconnected() }
            }
            Log.d("CommandClient", "disconnected: $message")
        }

        override fun writeGroups(message: OutboundGroupIterator?) {
            if (message == null) return
            val groups = mutableListOf<OutboundGroup>()
            while (message.hasNext()) {
                groups.add(message.next())
            }
            cachedGroups = groups
            getAllHandlers().forEach { it.updateGroups(groups) }
        }

        override fun writeOutbounds(message: OutboundGroupItemIterator?) {
            if (message == null) {
                return
            }
            val outbounds = mutableListOf<io.nekohasekai.libbox.OutboundGroupItem>()
            while (message.hasNext()) {
                outbounds.add(message.next())
            }
            cachedOutbounds = outbounds
            getAllHandlers().forEach { it.updateOutbounds(outbounds) }
        }

        override fun setDefaultLogLevel(level: Int) {
            getAllHandlers().forEach { it.setDefaultLogLevel(level) }
        }

        override fun clearLogs() {
            getAllHandlers().forEach { it.clearLogs() }
        }

        override fun writeLogs(messageList: LogIterator?) {
            if (messageList == null) return
            val logs = messageList.toList()
            getAllHandlers().forEach { it.appendLogs(logs) }
        }

        override fun writeStatus(message: StatusMessage) {
            getAllHandlers().forEach { it.updateStatus(message) }
        }

        override fun initializeClashMode(modeList: StringIterator, currentMode: String) {
            val modes = modeList.toList()
            getAllHandlers().forEach { it.initializeClashMode(modes, currentMode) }
        }

        override fun updateClashMode(newMode: String) {
            getAllHandlers().forEach { it.updateClashMode(newMode) }
        }

        override fun writeConnectionEvents(events: ConnectionEvents?) {
            if (events == null) return
            getAllHandlers().forEach { it.writeConnectionEvents(events) }
        }
    }
}

