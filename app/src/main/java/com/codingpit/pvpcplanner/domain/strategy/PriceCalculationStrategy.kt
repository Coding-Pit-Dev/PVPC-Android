package com.codingpit.pvpcplanner.domain.strategy

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot

interface PriceCalculationStrategy {
    fun calculateBestTimeSlot(device: Device, prices: List<PVPCModel>): TimeSlot
}