package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.ui.devices.DeviceRender
import javax.inject.Inject

data class ConsumptionSummary(
    val totalKWh: Double,
    val totalCost: Double,
)

class CalculateTotalConsumption @Inject constructor() {
    operator fun invoke(devices: List<DeviceRender>): ConsumptionSummary {
        if (devices.isEmpty()) {
            return ConsumptionSummary(0.0, 0.0)
        }

        // Calculate total kWh: sum of (watts * hours / 1000) for each device
        val totalKWh =
            devices.sumOf { device ->
                (device.device.watts * device.device.hours) / 1000.0
            }

        // Calculate total cost: sum of cost for each device
        val totalCost = devices.sumOf { it.cost }

        return ConsumptionSummary(totalKWh, totalCost)
    }
}
