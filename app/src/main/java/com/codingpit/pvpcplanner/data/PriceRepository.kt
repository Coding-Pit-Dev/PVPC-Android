package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.models.PVPCModel

interface PriceRepository {
    suspend fun getPrices(date: String): Result<List<PVPCModel>>

    suspend fun getPriceHistory(): Result<List<DailyPriceSummary>>
}
