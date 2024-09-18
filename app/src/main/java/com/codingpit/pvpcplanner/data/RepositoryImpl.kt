package com.codingpit.pvpcplanner.data

import android.util.Log
import com.codingpit.pvpcplanner.data.mappers.DTOConvertToModel
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domains.models.PVPCModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor (
    private val remoteDataSource: RemoteDataSource,
): Repository {
    override suspend fun getPrices(): List<PVPCModel> {
        try {
            Log.d("Repository", "Fetching data...")
            val responseRemote = remoteDataSource.getPrices()
            val responseUI = DTOConvertToModel().map(responseRemote)
            Log.d("Repository", "data fetched successfully")
            Log.w("Repository", responseRemote.toString())
            return responseUI
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching data", e)
            return emptyList()
        }
    }
}