package io.nekohasekai.sfa.compose.screen.dashboard

import io.nekohasekai.sfa.database.Profile

sealed class DashboardIntent {
    object ToggleService : DashboardIntent()
    data class SelectProfile(val id: Long) : DashboardIntent()
    data class EditProfile(val profile: Profile) : DashboardIntent()
    data class DeleteProfile(val profile: Profile) : DashboardIntent()
    data class UpdateProfile(val profile: Profile) : DashboardIntent()
    data class MoveProfile(val from: Int, val to: Int) : DashboardIntent()
    object DismissDeprecatedNote : DashboardIntent()
    data class ToggleCardVisibility(val cardGroup: CardGroup) : DashboardIntent()
    data class ReorderCards(val newOrder: List<CardGroup>) : DashboardIntent()
    object ResetCardOrder : DashboardIntent()
    object ToggleCardSettingsDialog : DashboardIntent()
    object CloseCardSettingsDialog : DashboardIntent()
    data class ToggleSystemProxy(val enabled: Boolean) : DashboardIntent()
    data class SelectClashMode(val mode: String) : DashboardIntent()
    object ShowAddProfileSheet : DashboardIntent()
    object HideAddProfileSheet : DashboardIntent()
    object ShowProfilePickerSheet : DashboardIntent()
    object HideProfilePickerSheet : DashboardIntent()
    data class ServiceStatusChanged(val status: io.nekohasekai.sfa.constant.Status) : DashboardIntent()
}
