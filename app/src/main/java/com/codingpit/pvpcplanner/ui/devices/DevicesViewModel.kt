package com.codingpit.pvpcplanner.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.handleErrors
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel
@Inject
constructor(
    getDevices: GetDevices,
    getPricesFlow: GetPricesFlow,
    private val addDevice: AddDevice,
    private val deleteDevice: DeleteDevice,
    private val calculateBestTimeSlot: CalculateBestTimeSlot,
    private val errorHandler: ErrorHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _state = MutableStateFlow(DevicesUIState())
    val state =
        combine(getDevices(), getPricesFlow(), _state) { devices, prices, _state ->
            prices.map { prices ->
                devices.map { DeviceRender(it, calculateBestTimeSlot(it, prices)) }
            } to _state
        }.map { DevicesState.Success(it.first.getOrThrow(), it.second.showModal) }
            .handleErrors(errorHandler, "device_data", DevicesState.Factory)
            .flowOn(dispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DevicesState.Loading)

    fun addDevice(
        name: String,
        hours: Int,
        icon: String,
    ) {
        viewModelScope.launch {
            addDevice.invoke(Device(name = name, hours = hours, icon = icon))
            hideAddDeviceModal()
        }
    }


    fun showAddDeviceModal() {
        viewModelScope.launch {
            _state.value = _state.value.copy(showModal = true)
        }
    }

    fun hideAddDeviceModal() {
        viewModelScope.launch {
            _state.value = _state.value.copy(showModal = null)
        }
    }

    fun removeDevice(device: Device) {
        viewModelScope.launch {
            withContext(dispatcher) {
                deleteDevice.invoke(device)
            }
        }
    }
}
