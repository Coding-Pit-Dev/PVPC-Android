package com.codingpit.pvpcplanner.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel
    @Inject
    constructor(
        getDevices: GetDevices,
        getPricesFlow: GetPricesFlow,
        private val addDevice: AddDevice,
    ) : ViewModel() {
        private val _state = MutableStateFlow<UIState>(UIState())
        val state =
            combine(getDevices(), getPricesFlow(), _state) { devices, prices, _state ->
                prices.map { prices ->
                    devices.map { DeviceRender(it, getBestSlot(it, prices)) }
                } to _state
            }.map { DevicesState.Success(it.first.getOrThrow(), it.second.showModal) }
                .flowOn(Dispatchers.IO)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DevicesState.Loading)

        fun addDevice(
            name: String,
            hours: Int,
            icon: String,
        ) {
            viewModelScope.launch {
                addDevice(Device(name = name, hours = hours, icon = icon))
                hideAddDeviceModal()
            }
        }

        private fun getBestSlot(
            device: Device,
            prices: List<PVPCModel>,
        ): TimeSlot {
            var bestSlot = prices.first().startHour
            var bestPrice = Double.MAX_VALUE

            for (index in 0 until prices.size - device.hours) {
                val slotPrice = prices.subList(index, index + device.hours).sumOf { it.pcb }
                if (slotPrice < bestPrice) {
                    bestPrice = slotPrice
                    bestSlot = prices[index].startHour
                }
            }

            return TimeSlot(bestSlot, bestSlot + device.hours)
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
    }

data class UIState(
    val showModal: Boolean? = null,
)
