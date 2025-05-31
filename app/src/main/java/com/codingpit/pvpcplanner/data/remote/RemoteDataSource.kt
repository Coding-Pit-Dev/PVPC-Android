package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domain.models.PVPCModel

interface RemoteDataSource {
    suspend fun getPrices(date: String): List<PVPCModel>
}
