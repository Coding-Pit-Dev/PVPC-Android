package com.codingpit.pvpcplanner.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.handleErrors
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.CalculateDeviceCost
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
        private val calculateDeviceCost: CalculateDeviceCost,
        private val errorHandler: ErrorHandler,
        private val dispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val searchQuery = MutableStateFlow("")
        private val selectedCategory = MutableStateFlow<DeviceCategoryFilter?>(null)

        val state =
            combine(
                getDevices(),
                getPricesFlow(),
                searchQuery,
                selectedCategory,
            ) { devices, prices, query, category ->
                prices.map { fetchResult ->
                    val devicesSlot =
                        devices.map { device ->
                            val bestSlot = calculateBestTimeSlot(device, fetchResult.prices)
                            val cost = calculateDeviceCost(device, bestSlot, fetchResult.prices)
                            DeviceRender(device, bestSlot, cost)
                        }
                    DevicesState.Success(devicesSlot = devicesSlot, searchQuery = query, selectedCategory = category)
                }
            }.map { it.getOrThrow() }
                .handleErrors(errorHandler, "device_data", DevicesState.Factory)
                .flowOn(dispatcher)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DevicesState.Loading)

        fun updateSearchQuery(query: String) {
            searchQuery.value = query
        }

        fun selectCategory(category: DeviceCategoryFilter?) {
            selectedCategory.value = category
        }

        fun addDevice(
            name: String,
            hours: Int,
            icon: String,
        ) {
            viewModelScope.launch {
                addDevice.invoke(Device(name = name, hours = hours, icon = icon))
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
