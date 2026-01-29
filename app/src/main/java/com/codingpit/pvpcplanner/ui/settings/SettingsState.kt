package com.codingpit.pvpcplanner.ui.settings

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState

sealed class SettingsState {
    data object Loading : SettingsState()

    data class Success(
        val settings: List<SettingRender>,
    ) : SettingsState()

    data class Error(
        override val error: String,
    ) : SettingsState(),
        ErrorState

    companion object Factory : HasErrorState<SettingsState> {
        override fun createErrorState(errorResult: ErrorResult): SettingsState = Error(errorResult.message)
    }
}
