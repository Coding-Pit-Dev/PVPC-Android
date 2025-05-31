package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import kotlinx.coroutines.flow.Flow

interface Repository{
    suspend fun getPrices(date: String): Result<List<PVPCModel>>

    fun getDevices(): Flow<List<Device>>

    suspend fun addDevice(device: Device)

    suspend fun deleteDevice(device: Device)

    suspend fun updateDevice(device: Device)
}