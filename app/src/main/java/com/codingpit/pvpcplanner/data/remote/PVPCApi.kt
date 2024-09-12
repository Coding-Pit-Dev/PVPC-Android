package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel
import retrofit2.http.GET

interface PVPCApi {

    @GET("prices")
    suspend fun getPrices(): List<PVPCModel>
}
