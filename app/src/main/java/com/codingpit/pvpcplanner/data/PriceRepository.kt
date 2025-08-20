package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.PVPCModel

interface PriceRepository {
    suspend fun getPrices(date: String): Result<List<PVPCModel>>
}
