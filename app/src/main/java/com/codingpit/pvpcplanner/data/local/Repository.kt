package com.codingpit.pvpcplanner.data.local

import android.util.Log
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domains.models.PVPCModel

class Repository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {


    private suspend fun getData(): PVPCModel {
        try {
            val responseRemote = remoteDataSource.getData()

            return responseRemote
        } catch (e: Exception) {
            Log.e("Repository", "Error", e)
            return emptyList() // Or handle the error as needed
        }
    }
}