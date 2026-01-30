package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.DeviceConsumptionInput
import javax.inject.Inject

data class ConsumptionSummary(
    val totalKWh: Double,
    val totalCost: Double,
)

class CalculateTotalConsumption
    @Inject
    constructor() {
        operator fun invoke(devices: List<DeviceConsumptionInput>): ConsumptionSummary {
            // Calculate total kWh: sum of (watts * hours / 1000) for each device
            val totalKWh =
                devices.sumOf { device ->
                    (device.watts * device.hours) / 1000.0
                }

            // Calculate total cost: sum of cost for each device
            val totalCost = devices.sumOf { it.cost }

            return ConsumptionSummary(totalKWh, totalCost)
        }
    }
