package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import javax.inject.Inject

class PriceRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: RemoteDataSource,
        private val localDataSource: PriceLocalDataSource,
    ) : PriceRepository {
        override suspend fun getPrices(date: String): Result<List<PVPCModel>> =
            runCatching {
                val localPrices = localDataSource.getPrices(date)

                localPrices.ifEmpty {
                    val remotePrices = remoteDataSource.getPrices(date)
                    localDataSource.savePrices(remotePrices)
                    remotePrices
                }
            }

        override suspend fun getPriceHistory(): Result<List<DailyPriceSummary>> =
            runCatching {
                val days = localDataSource.getAvailableDays()
                days.map { day ->
                    val prices = localDataSource.getPricesByStoredDay(day)
                    DailyPriceSummary(
                        day = day,
                        averagePrice = if (prices.isEmpty()) 0.0 else prices.sumOf { it.pcb } / prices.size,
                        minPrice = prices.minOfOrNull { it.pcb } ?: 0.0,
                        maxPrice = prices.maxOfOrNull { it.pcb } ?: 0.0,
                        hourCount = prices.size,
                    )
                }
            }
    }
