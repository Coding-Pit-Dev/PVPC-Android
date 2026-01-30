package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.DeviceConsumptionInput
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
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 1000, hours = 2, cost = 0.30),
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
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 1000, hours = 2, cost = 0.30),
                DeviceConsumptionInput(watts = 500, hours = 3, cost = 0.45),
                DeviceConsumptionInput(watts = 2000, hours = 1, cost = 0.50),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(5.5, result.totalKWh, 0.001)
        assertEquals(1.25, result.totalCost, 0.001)
    }

    @Test
    fun `handles devices with zero watts`() {
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 1000, hours = 2, cost = 0.30),
                DeviceConsumptionInput(watts = 0, hours = 3, cost = 0.00),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(2.0, result.totalKWh, 0.001)
        assertEquals(0.30, result.totalCost, 0.001)
    }

    @Test
    fun `handles low power devices correctly`() {
        // 100W * 10 hours = 1000Wh = 1 kWh
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 100, hours = 10, cost = 0.15),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(1.0, result.totalKWh, 0.001)
        assertEquals(0.15, result.totalCost, 0.001)
    }

    @Test
    fun `handles high power devices correctly`() {
        // 5000W * 1 hour = 5000Wh = 5 kWh
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 5000, hours = 1, cost = 1.50),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(5.0, result.totalKWh, 0.001)
        assertEquals(1.50, result.totalCost, 0.001)
    }

    @Test
    fun `calculates fractional kWh correctly`() {
        // 750W * 2 hours = 1500Wh = 1.5 kWh
        val devices =
            listOf(
                DeviceConsumptionInput(watts = 750, hours = 2, cost = 0.225),
            )

        val result = calculateTotalConsumption(devices)

        assertEquals(1.5, result.totalKWh, 0.001)
        assertEquals(0.225, result.totalCost, 0.001)
    }
}
