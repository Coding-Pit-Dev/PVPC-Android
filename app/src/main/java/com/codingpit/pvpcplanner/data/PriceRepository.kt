package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.PriceFetchResult

interface PriceRepository {
    suspend fun getPrices(date: String): Result<PriceFetchResult>
}
