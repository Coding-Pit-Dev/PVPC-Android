package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import javax.inject.Inject

class CalculateDeviceCost
    @Inject
    constructor() {
        operator fun invoke(
            device: Device,
            timeSlot: TimeSlot,
            prices: List<PVPCModel>,
        ): Double {
            if (device.watts == 0) return 0.0

            // Get prices for the time slot
            val slotPrices =
                prices.filter { price ->
                    price.startHour >= timeSlot.startHour && price.startHour < timeSlot.endHour
                }

            if (slotPrices.isEmpty()) return 0.0

            // Calculate energy consumed per hour in kWh
            val kWhPerHour = device.watts / 1000.0

            // Calculate total cost: sum of (kWh per hour * price per hour)
            val totalCost = slotPrices.sumOf { kWhPerHour * it.pcb }

            return totalCost
        }
    }
