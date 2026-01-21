package com.codingpit.pvpcplanner.integration

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.PriceRepositoryImpl
import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.strategy.BestTimeSlotCalculationStrategy
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import com.codingpit.pvpcplanner.utils.DateChecker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class PriceCalculationIntegrationTest {

    private val mockRemoteDataSource = mockk<RemoteDataSource>()
    private val mockLocalDataSource = mockk<PriceLocalDataSource>()
    private val mockDateChecker = mockk<DateChecker>()

    private lateinit var priceRepository: PriceRepositoryImpl
    private lateinit var getPricesUseCase: GetPrices
    private lateinit var getPricesFlowUseCase: GetPricesFlow
    private lateinit var calculateBestTimeSlotUseCase: CalculateBestTimeSlot

    private val strategy = BestTimeSlotCalculationStrategy()

    @Before
    fun setup() {
        priceRepository = PriceRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)
        getPricesUseCase = GetPrices(priceRepository)
        getPricesFlowUseCase = GetPricesFlow(priceRepository, mockDateChecker)
        calculateBestTimeSlotUseCase = CalculateBestTimeSlot(strategy)
    }

    @Test
    fun `complete workflow - fetch prices and calculate best time slot for device`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val washingMachine = Device(1, "Washing Machine", 2, "washing_machine")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.20, 0.23),  // Expensive
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.13),  // Cheap
            PVPCModel("2023-10-15", 2, 3, 0.08, 0.11),  // Cheapest
            PVPCModel("2023-10-15", 3, 4, 0.15, 0.18),  // Moderate
            PVPCModel("2023-10-15", 4, 5, 0.25, 0.28)   // Very expensive
        )

        coEvery { mockLocalDataSource.getPrices(date) } returns prices

        // Act
        val priceResult = getPricesUseCase(date)
        val bestTimeSlot = calculateBestTimeSlotUseCase(washingMachine, priceResult.getOrThrow())

        // Assert
        assertTrue(priceResult.isSuccess)
        assertEquals(5, priceResult.getOrNull()?.size)

        // Best slot for 2-hour device should be hours 1-3 (0.10 + 0.08 = 0.18 total)
        assertEquals(TimeSlot(1, 3), bestTimeSlot)
    }

    @Test
    fun `price flow integration with date checker`() = runTest {
        // Arrange
        val defaultDate = LocalDate.of(2023, 10, 15)
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.14, 0.17)
        )

        every { mockDateChecker.getDefaultDate() } returns defaultDate
        coEvery { mockLocalDataSource.getPrices("2023-10-15") } returns prices

        // Act & Assert
        getPricesFlowUseCase().test {
            val item = awaitItem()
            assertTrue(item.isSuccess)
            assertEquals(2, item.getOrNull()?.size)
            awaitComplete()
        }
    }

    @Test
    fun `complete device optimization scenario - multiple devices different hours`() = runTest {
        // Arrange - Realistic price pattern (high during day, low at night)
        val date = "2023-10-15"
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.12, 0.15),   // Night - cheap
            PVPCModel("2023-10-15", 1, 2, 0.11, 0.14),   // Night - cheaper
            PVPCModel("2023-10-15", 2, 3, 0.10, 0.13),   // Night - cheapest
            PVPCModel("2023-10-15", 3, 4, 0.11, 0.14),   // Early morning
            PVPCModel("2023-10-15", 4, 5, 0.13, 0.16),   // Morning
            PVPCModel("2023-10-15", 5, 6, 0.15, 0.18),   // Morning
            PVPCModel("2023-10-15", 6, 7, 0.20, 0.23),   // Day - expensive
            PVPCModel("2023-10-15", 7, 8, 0.25, 0.28),   // Day - very expensive
            PVPCModel("2023-10-15", 8, 9, 0.23, 0.26),   // Day - expensive
            PVPCModel("2023-10-15", 9, 10, 0.18, 0.21),  // Day - moderate
            PVPCModel("2023-10-15", 10, 11, 0.16, 0.19), // Day - moderate
            PVPCModel("2023-10-15", 11, 12, 0.14, 0.17)  // Noon - cheaper
        )

        val devices = listOf(
            Device(1, "Quick Wash", 1, "washing_machine"),      // 1 hour
            Device(2, "Dishwasher", 2, "dishwasher"),           // 2 hours  
            Device(3, "Dryer", 3, "dryer")                      // 3 hours
        )

        coEvery { mockLocalDataSource.getPrices(date) } returns prices

        // Act
        val priceResult = getPricesUseCase(date)
        assertTrue(priceResult.isSuccess)

        val quickWashSlot = calculateBestTimeSlotUseCase(devices[0], prices)
        val dishwasherSlot = calculateBestTimeSlotUseCase(devices[1], prices)
        val dryerSlot = calculateBestTimeSlotUseCase(devices[2], prices)

        // Assert - All devices should prefer early morning hours (cheapest)
        assertEquals(TimeSlot(2, 3), quickWashSlot)     // Cheapest single hour
        assertEquals(TimeSlot(1, 3), dishwasherSlot)    // Best 2-hour slot
        assertEquals(TimeSlot(1, 4), dryerSlot)         // Best 3-hour slot
    }

    @Test
    fun `price calculation with edge case - device longer than available prices`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val limitedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.14, 0.17)
        )
        val longRunningDevice =
            Device(1, "Long Device", 5, "device") // 5 hours but only 2 hours available

        coEvery { mockLocalDataSource.getPrices(date) } returns limitedPrices

        // Act
        val priceResult = getPricesUseCase(date)

        // The strategy should handle this gracefully - in real implementation it would
        // either return the maximum available slot or handle the edge case
        val bestTimeSlot = calculateBestTimeSlotUseCase(longRunningDevice, limitedPrices)

        // Assert
        assertTrue(priceResult.isSuccess)
        // The strategy implementation determines how this edge case is handled
        // This test verifies it doesn't crash
        assertTrue(bestTimeSlot.startHour >= 0)
        assertTrue(bestTimeSlot.endHour >= bestTimeSlot.startHour)
    }

    @Test
    fun `complete workflow with remote fetch and calculation`() = runTest {
        // Arrange - Simulate cache miss scenario
        val date = "2023-10-15"
        val device = Device(1, "Washing Machine", 2, "washing_machine")
        val remotePrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.18, 0.21),
            PVPCModel("2023-10-15", 1, 2, 0.12, 0.15),  // Cheapest
            PVPCModel("2023-10-15", 2, 3, 0.10, 0.13),  // Cheapest
            PVPCModel("2023-10-15", 3, 4, 0.16, 0.19)
        )

        coEvery { mockLocalDataSource.getPrices(date) } returns emptyList()
        coEvery { mockRemoteDataSource.getPrices(date) } returns remotePrices
        coEvery { mockLocalDataSource.savePrices(remotePrices) } returns Unit

        // Act
        val priceResult = getPricesUseCase(date)
        val bestSlot = calculateBestTimeSlotUseCase(device, priceResult.getOrThrow())

        // Assert
        assertTrue(priceResult.isSuccess)
        assertEquals(remotePrices, priceResult.getOrNull())
        assertEquals(TimeSlot(1, 3), bestSlot) // Hours 1-2 are cheapest (0.12 + 0.10 = 0.22)
    }

    @Test
    fun `price calculation handles equal prices correctly`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val device = Device(1, "Device", 2, "icon")
        val equalPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.15, 0.18),
            PVPCModel("2023-10-15", 2, 3, 0.15, 0.18),
            PVPCModel("2023-10-15", 3, 4, 0.15, 0.18)
        )

        coEvery { mockLocalDataSource.getPrices(date) } returns equalPrices

        // Act
        val priceResult = getPricesUseCase(date)
        val bestSlot = calculateBestTimeSlotUseCase(device, equalPrices)

        // Assert
        assertTrue(priceResult.isSuccess)
        // When all prices are equal, should return first available slot
        assertEquals(TimeSlot(0, 2), bestSlot)
    }

    @Test
    fun `flow integration with error recovery`() = runTest {
        // Arrange
        val defaultDate = LocalDate.of(2023, 10, 15)
        val exception = RuntimeException("Network error")

        every { mockDateChecker.getDefaultDate() } returns defaultDate
        coEvery { mockLocalDataSource.getPrices("2023-10-15") } returns emptyList()
        coEvery { mockRemoteDataSource.getPrices("2023-10-15") } throws exception

        // Act & Assert
        getPricesFlowUseCase().test {
            val item = awaitItem()
            assertTrue(item.isFailure)
            assertEquals(exception, item.exceptionOrNull())
            awaitComplete()
        }
    }
}
