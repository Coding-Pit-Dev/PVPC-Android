package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.domain.models.PVPCModel

interface PriceLocalDataSource {
    suspend fun getPrices(date: String): List<PVPCModel>
    suspend fun savePrices(pcpcModel: List<PVPCModel>)
}
