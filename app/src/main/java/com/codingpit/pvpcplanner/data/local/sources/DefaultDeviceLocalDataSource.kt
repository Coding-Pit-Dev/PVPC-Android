package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toEntity
import com.codingpit.pvpcplanner.domain.models.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultDeviceLocalDataSource @Inject constructor(
    private val pvpcDao: PVPCDao,
) : DeviceLocalDataSource {
    override fun getDevices(): Flow<List<Device>> =
        pvpcDao.getDevices().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveDevice(device: Device) {
        pvpcDao.insertDevice(device.toEntity())
    }

    override suspend fun updateDevice(device: Device) {
        pvpcDao.updateDevice(device.toEntity())
    }

    override suspend fun deleteDevice(device: Device) {
        pvpcDao.deleteDevice(device.toEntity())
    }
}
