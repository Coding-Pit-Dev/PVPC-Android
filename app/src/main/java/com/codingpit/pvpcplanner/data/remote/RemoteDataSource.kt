package com.codingpit.pvpcplanner.data.remote

import android.util.Log
import com.codingpit.pvpcplanner.domains.models.PVPCModel

class RemoteDataSource (
    private val api: PVPC_Api,
) {

    suspend fun getData(): PVPCModel {
        return api.getData()
    }
}