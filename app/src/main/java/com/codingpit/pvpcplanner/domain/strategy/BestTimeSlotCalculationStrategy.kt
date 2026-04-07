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
            // Also validate that we have enough consecutive slots
            // Assumption: prices are sorted by startHour.
            if (prices.size < device.hours) {
                return TimeSlot(prices.first().startHour, prices.last().endHour)
            }

            var bestSlot = prices.first().startHour
            var bestPrice = Double.MAX_VALUE

            // Ensure we don't go out of bounds and have enough data points
            val searchLimit = prices.size - device.hours

            for (index in 0..searchLimit) {
                val subList = prices.subList(index, index + device.hours)
                val slotPrice = subList.sumOf { it.pcb }
                if (slotPrice < bestPrice) {
                    bestPrice = slotPrice
                    bestSlot = prices[index].startHour
                }
            }

            // Ensure end hour doesn't exceed 24 (or the end of the day)
            val endHour = (bestSlot + device.hours).coerceAtMost(24)

            return TimeSlot(bestSlot, endHour)
        }
    }
