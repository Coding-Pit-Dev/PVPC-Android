package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCDTO
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val api: PVPCApi) : RemoteDataSource {

    override suspend fun getPrices(): List<PVPCDTO> {
        return api.getPrices().PVPC
    }
}
