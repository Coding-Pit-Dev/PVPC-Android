package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getPrices(date: String): List<PVPCModel>

    suspend fun savePrices(pcpcModel: List<PVPCModel>)

    fun getDevices(): Flow<List<Device>>

    suspend fun saveDevice(device: Device)

    suspend fun updateDevice(device: Device)

    suspend fun deleteDevice(device: Device)
}
