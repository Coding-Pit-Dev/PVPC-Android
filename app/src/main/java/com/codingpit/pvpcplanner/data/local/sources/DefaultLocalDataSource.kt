package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toEntity
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultLocalDataSource
    @Inject
    constructor(
        private val pvpcDao: PVPCDao,
    ) : LocalDataSource {
        override suspend fun getPrices(date: String): List<PVPCModel> {
            val parsedDate = date.split("-").reversed().joinToString("/")
            return pvpcDao.getPrices(parsedDate).map { it.toDomain() }
        }

        override suspend fun savePrices(pcpcModel: List<PVPCModel>) {
            pvpcDao.insertAll(pcpcModel.map { it.toEntity() })
        }

        override fun getDevices(): Flow<List<Device>> {
            return pvpcDao.getDevices().map { it.map { it.toDomain() } }
        }

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
