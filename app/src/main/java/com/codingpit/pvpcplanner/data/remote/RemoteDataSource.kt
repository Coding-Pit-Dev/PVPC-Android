package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel

interface RemoteDataSource {
    suspend fun getPrices(): List<PVPCModel>
}
