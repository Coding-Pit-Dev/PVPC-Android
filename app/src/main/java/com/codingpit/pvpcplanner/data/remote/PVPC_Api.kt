package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel

interface PVPC_Api {

    @GET("shows/search/filters?country=us&year_max=2020&series_granularity=episode&year_min=2020&genres_relation=and&output_language=en&catalogs=netflix&show_type=movie")
    suspend fun getData(): PVPCModel
}