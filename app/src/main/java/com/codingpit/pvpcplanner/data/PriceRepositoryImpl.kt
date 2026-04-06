package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult
import javax.inject.Inject

class PriceRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: PriceLocalDataSource,
    ) : PriceRepository {
        override suspend fun getPrices(date: String): Result<PriceFetchResult> =
            runCatching {
                val localPrices = localDataSource.getPrices(date)
                if (localPrices.isNotEmpty()) {
                    PriceFetchResult(prices = localPrices, isFromCache = true)
                } else {
                    val remotePrices = remoteDataSource.getPrices(date)
                    localDataSource.savePrices(remotePrices)
                    PriceFetchResult(prices = remotePrices, isFromCache = false)
                }
            }
    }
