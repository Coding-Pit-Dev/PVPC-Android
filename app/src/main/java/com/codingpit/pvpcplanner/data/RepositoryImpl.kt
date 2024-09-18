package com.codingpit.pvpcplanner.data

import android.util.Log
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domains.models.PVPCDTO
import javax.inject.Inject

class RepositoryImpl @Inject constructor (
    private val remoteDataSource: RemoteDataSource,
): Repository {
    override suspend fun getPrices(): List<PVPCDTO> {
        try {
            Log.d("Repository", "Fetching data...")
            val responseRemote = remoteDataSource.getPrices()
            Log.d("Repository", "data fetched successfully")
            Log.w("Repository", responseRemote.toString())
            return responseRemote
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching data", e)
            return emptyList() // Or handle the error as needed
        }
    }
}