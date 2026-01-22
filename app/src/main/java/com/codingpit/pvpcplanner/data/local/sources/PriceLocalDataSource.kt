package com.codingpit.pvpcplanner.data.local.sources

import com.codingpit.pvpcplanner.domain.models.PVPCModel

interface PriceLocalDataSource {
    /**
     * Returns the prices for the given date.
     * @param date Date in format YYYY-MM-DD
     * @return List of [PVPCModel]
     */
    suspend fun getPrices(date: String): List<PVPCModel>

    /**
     * Saves the prices in the local database.
     * @param pvpcModel List of [PVPCModel] to save
     */
    suspend fun savePrices(pvpcModel: List<PVPCModel>)
}
