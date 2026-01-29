package com.codingpit.pvpcplanner.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.UpdateDevice
import com.codingpit.pvpcplanner.utils.DeviceCategory
import com.codingpit.pvpcplanner.utils.DeviceIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceAddViewModel
    @Inject
    constructor(
        private val addDevice: AddDevice,
        private val updateDevice: UpdateDevice,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : ViewModel() {
        private val _state = MutableStateFlow<DeviceAddState>(DeviceAddState.Loading)
        val state: StateFlow<DeviceAddState> = _state.asStateFlow()

        fun initialize(
            availableIcons: List<DeviceIcon>,
            availableCategories: List<DeviceCategory>,
        ) {
            _state.value =
                DeviceAddState.Success(
                    availableIcons = availableIcons,
                    selectedIcon = availableIcons.firstOrNull(),
                    availableCategories = availableCategories,
                    selectedCategory = availableCategories.firstOrNull(),
                    saveEnabled = false,
                )
        }

        fun initializeForEdit(
            device: Device,
            availableIcons: List<DeviceIcon>,
            availableCategories: List<DeviceCategory>,
        ) {
            val selectedIcon = availableIcons.find { it.id == device.icon }
            val selectedCategory = availableCategories.find { it.id == device.category }

            _state.value =
                DeviceAddState.Success(
                    deviceId = device.id,
                    isEditMode = true,
                    deviceName = device.name,
                    watts = device.watts.toString(),
                    hours = device.hours.toString(),
                    alwaysOn = device.hours == 24,
                    notes = "",
                    availableIcons = availableIcons,
                    selectedIcon = selectedIcon ?: availableIcons.firstOrNull(),
                    availableCategories = availableCategories,
                    selectedCategory = selectedCategory ?: availableCategories.firstOrNull(),
                    saveEnabled = true,
                )
        }

        fun onDeviceNameChanged(name: String) {
            updateSuccessState { copy(deviceName = name) }
            validateForm()
        }

        fun onWattsChanged(watts: String) {
            updateSuccessState { copy(watts = watts) }
            validateForm()
        }

        fun onHoursChanged(hours: String) {
            updateSuccessState { copy(hours = hours) }
            validateHours()
            validateForm()
        }

        fun onAlwaysOnChanged(alwaysOn: Boolean) {
            updateSuccessState {
                copy(
                    alwaysOn = alwaysOn,
                    hours = if (alwaysOn) "24" else hours,
                )
            }
            validateForm()
        }

        fun onNotesChanged(notes: String) {
            updateSuccessState { copy(notes = notes) }
        }

        fun onIconSelected(icon: DeviceIcon) {
            updateSuccessState {
                copy(
                    selectedIcon = icon,
                    showIconPicker = false,
                )
            }
        }

        fun showIconPicker() {
            updateSuccessState { copy(showIconPicker = true) }
        }

        fun hideIconPicker() {
            updateSuccessState { copy(showIconPicker = false) }
        }

        fun onCategorySelected(category: DeviceCategory) {
            updateSuccessState {
                copy(
                    selectedCategory = category,
                    showCategoryPicker = false,
                )
            }
        }

        fun showCategoryPicker() {
            updateSuccessState { copy(showCategoryPicker = true) }
        }

        fun hideCategoryPicker() {
            updateSuccessState { copy(showCategoryPicker = false) }
        }

        fun saveDevice(onSuccess: () -> Unit) {
            val currentState = _state.value
            if (currentState !is DeviceAddState.Success || !currentState.saveEnabled) {
                return
            }

            viewModelScope.launch {
                updateSuccessState { copy(isSaving = true) }

                try {
                    val hours = currentState.hours.toIntOrNull() ?: 0
                    val watts = (currentState.watts.toDoubleOrNull() ?: 0.0).toInt()
                    val device =
                        Device(
                            id = currentState.deviceId ?: 0,
                            name = currentState.deviceName,
                            hours = hours,
                            icon = currentState.selectedIcon?.id ?: "",
                            watts = watts,
                            category = currentState.selectedCategory?.id ?: "appliances",
                        )

                    withContext(dispatcher) {
                        if (currentState.isEditMode) {
                            updateDevice.invoke(device)
                        } else {
                            addDevice.invoke(device)
                        }
                    }

                    onSuccess()
                } catch (e: Exception) {
                    _state.value =
                        DeviceAddState.Error(
                            error = e.message ?: "Error saving device",
                        )
                }
            }
        }

        private fun validateHours() {
            updateSuccessState {
                val hoursValue = hours.toIntOrNull()
                val error = when {
                    hoursValue == null || hoursValue <= 0 -> null // Will be caught by validateForm
                    hoursValue > 24 -> "error_validation_device_hours_max"
                    else -> null
                }
                copy(hoursError = error)
            }
        }

        private fun validateForm() {
            updateSuccessState {
                val hoursValue = hours.toIntOrNull()
                val wattsValue = watts.toDoubleOrNull()

                val isValid =
                    deviceName.isNotEmpty() &&
                        hoursValue != null &&
                        hoursValue > 0 &&
                        hoursValue <= 24 &&
                        wattsValue != null &&
                        wattsValue > 0 &&
                        hoursError == null

                copy(saveEnabled = isValid)
            }
        }

        private inline fun updateSuccessState(update: DeviceAddState.Success.() -> DeviceAddState.Success) {
            _state.update { state ->
                if (state is DeviceAddState.Success) {
                    state.update()
                } else {
                    state
                }
            }
        }
    }
