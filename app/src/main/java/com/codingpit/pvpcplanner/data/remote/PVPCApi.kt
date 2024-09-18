package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCResponse
import retrofit2.http.GET

interface PVPCApi {

    @GET("archives/70/download_json")
    suspend fun getPrices(): PVPCResponse
}
