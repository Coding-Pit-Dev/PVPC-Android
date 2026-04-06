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

    /**
     * Returns the list of days that have cached price data, ordered by most recent first.
     * Days are returned in the stored format (dd/MM/yyyy).
     * @return List of day strings (up to 30)
     */
    fun getAvailableDays(): List<String>

    /**
     * Returns the prices for the given day using the stored date format directly.
     * @param storedDay Day string in the stored format (dd/MM/yyyy)
     * @return List of [PVPCModel]
     */
    fun getPricesByStoredDay(storedDay: String): List<PVPCModel>
}
