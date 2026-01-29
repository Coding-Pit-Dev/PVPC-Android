package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState
import com.codingpit.pvpcplanner.utils.DeviceCategory
import com.codingpit.pvpcplanner.utils.DeviceIcon

sealed class DeviceAddState {
    object Loading : DeviceAddState()

    data class Success(
        val deviceId: Int? = null,
        val isEditMode: Boolean = false,
        val deviceName: String = "",
        val watts: String = "",
        val hours: String = "",
        val alwaysOn: Boolean = false,
        val notes: String = "",
        val selectedIcon: DeviceIcon? = null,
        val availableIcons: List<DeviceIcon> = emptyList(),
        val selectedCategory: DeviceCategory? = null,
        val availableCategories: List<DeviceCategory> = emptyList(),
        val showIconPicker: Boolean = false,
        val showCategoryPicker: Boolean = false,
        val isSaving: Boolean = false,
        val saveEnabled: Boolean = false,
        val hoursError: String? = null,
    ) : DeviceAddState()

    data class Error(
        override val error: String,
    ) : DeviceAddState(),
        ErrorState

    companion object Factory : HasErrorState<DeviceAddState> {
        override fun createErrorState(errorResult: ErrorResult): DeviceAddState =
            Error(errorResult.message)
    }
}
