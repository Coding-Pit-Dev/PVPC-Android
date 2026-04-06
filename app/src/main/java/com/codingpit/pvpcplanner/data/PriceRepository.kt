package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult

interface PriceRepository {
    suspend fun getPrices(date: String): Result<PriceFetchResult>

    suspend fun getPriceHistory(): Result<List<DailyPriceSummary>>
}
