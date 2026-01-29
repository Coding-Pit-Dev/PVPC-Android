package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.ui.devices.DeviceRender
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateTotalConsumptionTest {
    private lateinit var calculateTotalConsumption: CalculateTotalConsumption

    @Before
    fun setup() {
        calculateTotalConsumption = CalculateTotalConsumption()
    }

    @Test
    fun `returns zero for empty device list`() {
        val result = calculateTotalConsumption(emptyList())

        assertEquals(0.0, result.totalKWh, 0.001)
        assertEquals(0.0, result.totalCost, 0.001)
    }

    @Test
    fun `calculates total kWh and cost for single device`() {
        // 1000W * 2 hours = 2000Wh = 2 kWh
        val device = Device(1, "Test Device", 2, "test_icon", watts = 1000)
        val devices =
            listOf(
                DeviceRender(device, TimeSlot(0, 2), cost = 0.30),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(2.0, result.totalKWh, 0.001)
        assertEquals(0.30, result.totalCost, 0.001)
    }

    @Test
    fun `calculates total kWh and cost for multiple devices`() {
        // Device 1: 1000W * 2 hours = 2 kWh
        // Device 2: 500W * 3 hours = 1.5 kWh
        // Device 3: 2000W * 1 hour = 2 kWh
        // Total: 5.5 kWh
        val device1 = Device(1, "Device 1", 2, "icon1", watts = 1000)
        val device2 = Device(2, "Device 2", 3, "icon2", watts = 500)
        val device3 = Device(3, "Device 3", 1, "icon3", watts = 2000)

        val devices =
            listOf(
                DeviceRender(device1, TimeSlot(0, 2), cost = 0.30),
                DeviceRender(device2, TimeSlot(2, 5), cost = 0.45),
                DeviceRender(device3, TimeSlot(5, 6), cost = 0.50),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(5.5, result.totalKWh, 0.001)
        assertEquals(1.25, result.totalCost, 0.001)
    }

    @Test
    fun `handles devices with zero watts`() {
        val device1 = Device(1, "Device 1", 2, "icon1", watts = 1000)
        val device2 = Device(2, "Device 2", 3, "icon2", watts = 0)

        val devices =
            listOf(
                DeviceRender(device1, TimeSlot(0, 2), cost = 0.30),
                DeviceRender(device2, TimeSlot(2, 5), cost = 0.00),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(2.0, result.totalKWh, 0.001)
        assertEquals(0.30, result.totalCost, 0.001)
    }

    @Test
    fun `handles low power devices correctly`() {
        // 100W * 10 hours = 1000Wh = 1 kWh
        val device = Device(1, "Light Bulb", 10, "bulb", watts = 100)
        val devices =
            listOf(
                DeviceRender(device, TimeSlot(0, 10), cost = 0.15),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(1.0, result.totalKWh, 0.001)
        assertEquals(0.15, result.totalCost, 0.001)
    }

    @Test
    fun `handles high power devices correctly`() {
        // 5000W * 1 hour = 5000Wh = 5 kWh
        val device = Device(1, "Electric Heater", 1, "heater", watts = 5000)
        val devices =
            listOf(
                DeviceRender(device, TimeSlot(0, 1), cost = 1.50),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(5.0, result.totalKWh, 0.001)
        assertEquals(1.50, result.totalCost, 0.001)
    }

    @Test
    fun `calculates fractional kWh correctly`() {
        // 750W * 2 hours = 1500Wh = 1.5 kWh
        val device = Device(1, "Device", 2, "icon", watts = 750)
        val devices =
            listOf(
                DeviceRender(device, TimeSlot(0, 2), cost = 0.225),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(1.5, result.totalKWh, 0.001)
        assertEquals(0.225, result.totalCost, 0.001)
    }
}
