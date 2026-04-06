package com.codingpit.pvpcplanner.domain.models

data class PriceFetchResult(
    val prices: List<PVPCModel>,
    val isFromCache: Boolean,
)
