package com.codingpit.pvpcplanner.domain.models

data class DailyPriceSummary(
    val day: String,
    val averagePrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val hourCount: Int,
)
