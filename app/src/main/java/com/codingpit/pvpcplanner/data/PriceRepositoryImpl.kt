package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.LocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import javax.inject.Inject

class PriceRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : PriceRepository {

    override suspend fun getPrices(date: String): Result<List<PVPCModel>> {
        return runCatching {
            val localPrices = localDataSource.getPrices(date)

            localPrices.ifEmpty {
                val remotePrices = remoteDataSource.getPrices(date)
                localDataSource.savePrices(remotePrices)
                remotePrices
            }
        }
    }
}