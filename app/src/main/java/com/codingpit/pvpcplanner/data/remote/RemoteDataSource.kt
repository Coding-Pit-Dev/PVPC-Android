package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCDTO

interface RemoteDataSource {
    suspend fun getPrices(): List<PVPCDTO>
}
