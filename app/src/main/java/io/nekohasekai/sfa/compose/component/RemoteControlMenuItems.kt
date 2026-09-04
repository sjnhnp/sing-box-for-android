package io.nekohasekai.sfa.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.nekohasekai.sfa.database.RemoteServer

const val REMOTE_CONTROL_ROUTE = "settings/remote_control"

@Composable
fun rememberRemoteServers(): State<List<RemoteServer>> =
    remember { mutableStateOf(emptyList()) }

@Composable
fun RemoteControlMenuItems(servers: List<RemoteServer>, onAction: () -> Unit, leadingDivider: Boolean = true) {
}

