package com.codingpit.pvpcplanner.domain.strategy

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import javax.inject.Inject

class BestTimeSlotCalculationStrategy
    @Inject
    constructor() : PriceCalculationStrategy {
        override fun calculateBestTimeSlot(
            device: Device,
            prices: List<PVPCModel>,
        ): TimeSlot {
            require(prices.isNotEmpty()) { "Prices list cannot be empty" }
            require(device.hours > 0) { "Device hours must be positive" }
            require(prices.size >= device.hours) {
                "Not enough price data (${prices.size}) for device requiring ${device.hours} hours"
            }
            var bestSlot = prices.first().startHour
            var bestPrice = Double.MAX_VALUE

            for (index in 0..prices.size - device.hours) {
                val slotPrice = prices.subList(index, index + device.hours).sumOf { it.pcb }
                if (slotPrice < bestPrice) {
                    bestPrice = slotPrice
                    bestSlot = prices[index].startHour
                }
            }

            return TimeSlot(bestSlot, bestSlot + device.hours)
        }
    }
