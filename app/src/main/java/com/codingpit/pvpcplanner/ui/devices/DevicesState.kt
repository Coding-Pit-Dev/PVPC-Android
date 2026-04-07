package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState

sealed class DevicesState {
    object Loading : DevicesState()

    data class Success(
        val devicesSlot: List<DeviceRender> = emptyList(),
        val searchQuery: String = "",
        val selectedCategory: DeviceCategoryFilter? = null,
    ) : DevicesState() {
        val filteredDevices: List<DeviceRender>
            get() {
                var result = devicesSlot
                if (searchQuery.isNotBlank()) {
                    result = result.filter { it.device.name.contains(searchQuery, ignoreCase = true) }
                }
                if (selectedCategory != null) {
                    result = result.filter { it.device.category == selectedCategory.id }
                }
                return result
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
