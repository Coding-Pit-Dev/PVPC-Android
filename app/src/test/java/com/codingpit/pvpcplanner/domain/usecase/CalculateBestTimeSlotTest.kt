package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.strategy.PriceCalculationStrategy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateBestTimeSlotTest {

    private val mockStrategy = mockk<PriceCalculationStrategy>()
    private lateinit var useCase: CalculateBestTimeSlot

    @Before
    fun setup() {
        useCase = CalculateBestTimeSlot(mockStrategy)
    }

    @Test
    fun `invoke calls strategy with correct parameters`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 2, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.18)
        )
        val expectedResult = TimeSlot(1, 3)
        every { mockStrategy.calculateBestTimeSlot(device, prices) } returns expectedResult

        // Act
        val result = useCase(device, prices)

        // Assert
        assertEquals(expectedResult, result)
        verify { mockStrategy.calculateBestTimeSlot(device, prices) }
    }

    @Test
    fun `invoke returns result from strategy`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 1, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18)
        )
        val expectedResult = TimeSlot(0, 1)
        every { mockStrategy.calculateBestTimeSlot(device, prices) } returns expectedResult

        // Act
        val result = useCase(device, prices)

        // Assert
        assertEquals(expectedResult, result)
    }
}