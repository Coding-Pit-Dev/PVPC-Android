package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDevices(): Flow<List<Device>>

    suspend fun addDevice(device: Device)

    suspend fun deleteDevice(device: Device)

    suspend fun updateDevice(device: Device)
}
