package com.codingpit.pvpcplanner.domain.strategy

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PriceCalculationStrategyTest {
    private lateinit var strategy: PriceCalculationStrategy

    @Before
    fun setup() {
        strategy = BestTimeSlotCalculationStrategy()
    }

    private fun priceAt(hour: Int, pcb: Double) = PVPCModel("01/05/2026", hour, hour + 1, pcb, 0.01)

    @Test
    fun `finds cheapest consecutive slot for 2-hour device`() {
        val prices =
            (0 until 24).map { h ->
                priceAt(h, if (h in 2..3) 0.05 else 0.15)
            }
        val device = Device(1, "Test", hours = 2, icon = "test")

        val slot = strategy.calculateBestTimeSlot(device, prices)

        assertEquals(2, slot.startHour)
        assertEquals(4, slot.endHour)
    }

    @Test
    fun `single hour device picks cheapest single hour`() {
        val prices =
            (0 until 24).map { h ->
                priceAt(h, if (h == 5) 0.01 else 0.20)
            }
        val device = Device(1, "Test", hours = 1, icon = "test")

        val slot = strategy.calculateBestTimeSlot(device, prices)

        assertEquals(5, slot.startHour)
        assertEquals(6, slot.endHour)
    }

    @Test
    fun `when prices fewer than device hours returns full available range`() {
        val prices = listOf(priceAt(0, 0.10), priceAt(1, 0.08))
        val device = Device(1, "Test", hours = 5, icon = "test")

        val slot = strategy.calculateBestTimeSlot(device, prices)

        assertEquals(0, slot.startHour)
        assertEquals(2, slot.endHour)
    }

    @Test
    fun `throws for empty price list`() {
        val device = Device(1, "Test", hours = 2, icon = "test")

        try {
            strategy.calculateBestTimeSlot(device, emptyList())
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Prices list cannot be empty", e.message)
        }
    }

    @Test
    fun `throws for device with zero hours`() {
        val prices = listOf(priceAt(0, 0.10))
        val device = Device(1, "Test", hours = 0, icon = "test")

        try {
            strategy.calculateBestTimeSlot(device, prices)
            org.junit.Assert.fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Device hours must be positive", e.message)
        }
    }

    @Test
    fun `end hour capped at 24`() {
        val prices = (0 until 24).map { h -> priceAt(h, if (h == 23) 0.01 else 0.20) }
        val device = Device(1, "Test", hours = 2, icon = "test")

        val slot = strategy.calculateBestTimeSlot(device, prices)

        assertTrue("endHour should not exceed 24", slot.endHour <= 24)
    }

    private fun assertTrue(message: String, condition: Boolean) {
        org.junit.Assert.assertTrue(message, condition)
    }
}
