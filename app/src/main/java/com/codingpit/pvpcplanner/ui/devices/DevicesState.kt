package com.codingpit.pvpcplanner.ui.devices

sealed class DevicesState {
    object Loading : DevicesState()

    data class Success(
        val devicesSlot: List<DeviceRender> = emptyList(),
    ) : DevicesState()

    data class Error(
        val error: String,
    ) : DevicesState()
}
