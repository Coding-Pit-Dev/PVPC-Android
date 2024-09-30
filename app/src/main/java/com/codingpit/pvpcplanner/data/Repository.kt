package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domains.models.PVPCModel

interface Repository{
    suspend fun getPrices(): List<PVPCModel>
}