package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateDeviceCostTest {
    private lateinit var calculateDeviceCost: CalculateDeviceCost

    @Before
    fun setup() {
        calculateDeviceCost = CalculateDeviceCost()
    }

    @Test
    fun `calculate cost for device with zero watts returns zero`() {
        val device = Device(1, "Test Device", 2, "test_icon", watts = 0)
        val timeSlot = TimeSlot(startHour = 0, endHour = 2)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                PVPCModel("2023-10-15", 1, 2, 0.20, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculate cost correctly for 1000W device over 2 hours`() {
        // 1000W = 1 kWh per hour
        // Hour 0: 1 kWh * 0.15 = 0.15
        // Hour 1: 1 kWh * 0.20 = 0.20
        // Total: 0.35
        val device = Device(1, "Test Device", 2, "test_icon", watts = 1000)
        val timeSlot = TimeSlot(startHour = 0, endHour = 2)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                PVPCModel("2023-10-15", 1, 2, 0.20, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        assertEquals(0.35, result, 0.001)
    }

    @Test
    fun `calculate cost correctly for 500W device over 3 hours`() {
        // 500W = 0.5 kWh per hour
        // Hour 0: 0.5 kWh * 0.10 = 0.05
        // Hour 1: 0.5 kWh * 0.15 = 0.075
        // Hour 2: 0.5 kWh * 0.20 = 0.10
        // Total: 0.225
        val device = Device(1, "Test Device", 3, "test_icon", watts = 500)
        val timeSlot = TimeSlot(startHour = 0, endHour = 3)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.10, 0.18),
                PVPCModel("2023-10-15", 1, 2, 0.15, 0.18),
                PVPCModel("2023-10-15", 2, 3, 0.20, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        assertEquals(0.225, result, 0.001)
    }

    @Test
    fun `calculate cost returns zero when no prices in time slot`() {
        val device = Device(1, "Test Device", 2, "test_icon", watts = 1000)
        val timeSlot = TimeSlot(startHour = 5, endHour = 7)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                PVPCModel("2023-10-15", 1, 2, 0.20, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculate cost correctly for device with partial time slot overlap`() {
        // Only hour 1 and 2 are in the time slot (1-3)
        val device = Device(1, "Test Device", 2, "test_icon", watts = 2000)
        val timeSlot = TimeSlot(startHour = 1, endHour = 3)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.30, 0.18),
                PVPCModel("2023-10-15", 1, 2, 0.15, 0.18),
                PVPCModel("2023-10-15", 2, 3, 0.20, 0.18),
                PVPCModel("2023-10-15", 3, 4, 0.25, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        // 2000W = 2 kWh per hour
        // Hour 1: 2 * 0.15 = 0.30
        // Hour 2: 2 * 0.20 = 0.40
        // Total: 0.70
        assertEquals(0.70, result, 0.001)
    }

    @Test
    fun `calculate cost correctly for low power device`() {
        // 100W = 0.1 kWh per hour
        val device = Device(1, "Test Device", 1, "test_icon", watts = 100)
        val timeSlot = TimeSlot(startHour = 0, endHour = 1)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 0, 1, 0.50, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        // 0.1 kWh * 0.50 = 0.05
        assertEquals(0.05, result, 0.001)
    }

    @Test
    fun `calculate cost correctly for midnight wrapping time slot`() {
        // 1000W = 1 kWh per hour
        // Slot: 23:00 to 01:00 (2 hours)
        // Hour 23: 1 kWh * 0.10 = 0.10
        // Hour 0: 1 kWh * 0.20 = 0.20
        // Total: 0.30
        val device = Device(1, "Test Device", 2, "test_icon", watts = 1000)
        val timeSlot = TimeSlot(startHour = 23, endHour = 1)
        val prices =
            listOf(
                PVPCModel("2023-10-15", 22, 23, 0.15, 0.18),
                PVPCModel("2023-10-15", 23, 0, 0.10, 0.18), // 23:00-00:00
                PVPCModel("2023-10-16", 0, 1, 0.20, 0.18),
                PVPCModel("2023-10-16", 1, 2, 0.25, 0.18),
            )

        val result = calculateDeviceCost(device, timeSlot, prices)

        assertEquals(0.30, result, 0.001)
    }
}
