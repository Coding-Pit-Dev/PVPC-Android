package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.DeviceLocalDataSource
import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val localDataSource: DeviceLocalDataSource,
) : DeviceRepository {

    override fun getDevices(): Flow<List<Device>> {
        return localDataSource.getDevices()
    }

    override suspend fun addDevice(device: Device) {
        localDataSource.saveDevice(device)
    }

    override suspend fun deleteDevice(device: Device) {
        localDataSource.deleteDevice(device)
    }

    override suspend fun updateDevice(device: Device) {
        localDataSource.updateDevice(device)
    }
}