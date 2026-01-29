package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState

sealed class DevicesState {
    object Loading : DevicesState()

    data class Success(
        val devicesSlot: List<DeviceRender> = emptyList(),
        val searchQuery: String = "",
    ) : DevicesState() {
        val filteredDevices: List<DeviceRender>
            get() =
                if (searchQuery.isBlank()) {
                    devicesSlot
                } else {
                    devicesSlot.filter { device ->
                        device.device.name.contains(searchQuery, ignoreCase = true)
                    }
                }
    }

    data class Error(
        override val error: String,
    ) : DevicesState(),
        ErrorState

    companion object Factory : HasErrorState<DevicesState> {
        override fun createErrorState(errorResult: ErrorResult): DevicesState = Error(errorResult.message)
    }
}
