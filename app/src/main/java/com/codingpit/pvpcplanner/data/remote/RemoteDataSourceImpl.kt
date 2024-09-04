package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val api: PVPCApi) : RemoteDataSource {

    override suspend fun getPrices(): List<PVPCModel> {
        return api.getPrices()
    }
}
