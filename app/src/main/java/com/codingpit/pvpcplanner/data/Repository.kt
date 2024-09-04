package com.codingpit.pvpcplanner.data

import android.util.Log
import com.codingpit.pvpcplanner.domains.models.PVPCModel
import javax.inject.Inject

class Repository @Inject constructor (
    private val remoteDataSource: RemoteDataSource,
) {

    suspend fun getHeroes(): List<PVPCModel> {
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