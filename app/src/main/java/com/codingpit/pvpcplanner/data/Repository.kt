package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domains.models.PVPCDTO

interface Repository{
    suspend fun getPrices(): List<PVPCDTO>
}