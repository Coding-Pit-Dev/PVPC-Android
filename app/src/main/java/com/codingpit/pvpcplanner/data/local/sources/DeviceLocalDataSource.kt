package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow

interface DeviceLocalDataSource {
    fun getDevices(): Flow<List<Device>>

    suspend fun saveDevice(device: Device)

    suspend fun updateDevice(device: Device)

    suspend fun deleteDevice(device: Device)
}
