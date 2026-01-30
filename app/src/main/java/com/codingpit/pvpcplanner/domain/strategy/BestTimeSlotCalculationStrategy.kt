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

            // If we don't have enough price data for the full duration, return the full available range
            if (prices.size < device.hours) {
                return TimeSlot(prices.first().startHour, prices.last().endHour)
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
