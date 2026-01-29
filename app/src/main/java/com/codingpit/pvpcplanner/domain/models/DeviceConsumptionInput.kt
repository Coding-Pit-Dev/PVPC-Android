package com.codingpit.pvpcplanner.domain.models

data class DeviceConsumptionInput(
    val watts: Int,
    val hours: Int,
    val cost: Double,
)
