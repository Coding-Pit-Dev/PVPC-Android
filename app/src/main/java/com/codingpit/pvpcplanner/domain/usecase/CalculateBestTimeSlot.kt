package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.strategy.PriceCalculationStrategy
import javax.inject.Inject

class CalculateBestTimeSlot @Inject constructor(
    private val priceCalculationStrategy: PriceCalculationStrategy
) {
    operator fun invoke(device: Device, prices: List<PVPCModel>): TimeSlot {
        return priceCalculationStrategy.calculateBestTimeSlot(device, prices)
    }
}