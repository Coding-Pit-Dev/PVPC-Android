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
            var bestSlot = prices.first().startHour
            var bestPrice = Double.MAX_VALUE

            for (index in 0 until prices.size - device.hours) {
                val slotPrice = prices.subList(index, index + device.hours).sumOf { it.pcb }
                if (slotPrice < bestPrice) {
                    bestPrice = slotPrice
                    bestSlot = prices[index].startHour
                }
            }

            return TimeSlot(bestSlot, bestSlot + device.hours)
        }
    }
