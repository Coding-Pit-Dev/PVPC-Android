package com.codingpit.pvpcplanner.ui.settings

sealed class SettingsState {
    data object Loading : SettingsState()

    data class Success(val settings: List<SettingRender>) : SettingsState()

    data class Error(val message: String) : SettingsState()
}
