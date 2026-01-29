package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val api: PVPCApi,
) : RemoteDataSource {
    override suspend fun getPrices(date: String): List<PVPCModel> =
        api.getPrices(date).pvpc.map { it.toDomain() }
}
