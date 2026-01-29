package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.TimeSlot

data class DeviceRender(
    val device: Device,
    val bestSlot: TimeSlot,
    val cost: Double = 0.0,
    val swiped: Boolean = false,
)
