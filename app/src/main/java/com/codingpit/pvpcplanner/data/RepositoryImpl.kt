package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.LocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : Repository {
    override suspend fun getPrices(date: String): Result<List<PVPCModel>> {
        return runCatching {
            val localPrices = localDataSource.getPrices(date)

            if (localPrices.isNotEmpty()) {
                localPrices
            } else {
                val remotePrices = remoteDataSource.getPrices(date)
                localDataSource.savePrices(remotePrices)
                remotePrices
            }
        }
    }

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
