package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel

class RemoteDataSource(
    private val api: PVPCApi
) {
    suspend fun getPrices(): List<PVPCModel> = api.getPrices()

}

