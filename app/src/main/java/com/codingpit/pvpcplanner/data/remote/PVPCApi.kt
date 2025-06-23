package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.data.remote.response.PVPCResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PVPCApi {
    @GET("archives/70/download_json")
    suspend fun getPrices(
        @Query("date") date: String,
    ): PVPCResponse
}
