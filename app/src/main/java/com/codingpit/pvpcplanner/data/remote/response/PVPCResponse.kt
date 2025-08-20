package com.codingpit.pvpcplanner.data.remote.response

import com.squareup.moshi.Json

data class PVPCResponse(
    @get:Json(name = "PVPC") val pvpc: List<PVPCDTO>,
)
