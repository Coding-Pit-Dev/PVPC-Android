package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.mappers.DTOConvertToModel
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domains.models.PVPCModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : Repository {
    override suspend fun getPrices(): List<PVPCModel> {
        return try {
            val responseRemote = remoteDataSource.getPrices()
            val responseUI = DTOConvertToModel().map(responseRemote)
            responseUI
        } catch (e: Exception) {
            emptyList()
        }
    }
}
