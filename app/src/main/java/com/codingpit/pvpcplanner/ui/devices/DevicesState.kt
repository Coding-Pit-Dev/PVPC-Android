package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState

sealed class DevicesState {
    object Loading : DevicesState()

    data class Success(
        val devicesSlot: List<DeviceRender> = emptyList(),
        val showModal: Boolean? = null,
    ) : DevicesState()

    data class Error(
        override val error: String,
    ) : DevicesState(), ErrorState

    companion object Factory : HasErrorState<DevicesState> {
        override fun createErrorState(errorResult: ErrorResult): DevicesState {
            return Error(errorResult.message)
        }
    }
}
