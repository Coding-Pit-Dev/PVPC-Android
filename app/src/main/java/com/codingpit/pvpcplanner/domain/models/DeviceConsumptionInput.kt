package com.codingpit.pvpcplanner.domain.models

/**
 * @param watts Power in watts (W).
 * @param hours Usage duration in hours.
 * @param cost Energy cost in billing units.
 */
data class DeviceConsumptionInput(
    val watts: Int,
    val hours: Int,
    val cost: Double,
)
