package com.codingpit.pvpcplanner.fixtures

import com.codingpit.pvpcplanner.data.PriceRepositoryImpl
import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult
import com.codingpit.pvpcplanner.domain.strategy.BestTimeSlotCalculationStrategy
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.fixtures.DeviceTestFixtures.CommonDevices
import com.codingpit.pvpcplanner.fixtures.PVPCTestFixtures.Models
import com.codingpit.pvpcplanner.fixtures.PVPCTestFixtures.TestDates
import com.codingpit.pvpcplanner.fixtures.SettingsTestFixtures.UserProfiles
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Example test demonstrating how to use the test fixtures for consistent and realistic testing.
 * This serves both as documentation and as a validation that the fixtures work correctly.
 */
class FixtureUsageExampleTest {
    private val mockRemoteDataSource = mockk<RemoteDataSource>()
    private val mockLocalDataSource = mockk<PriceLocalDataSource>()

    private lateinit var priceRepository: PriceRepositoryImpl
    private lateinit var getPricesUseCase: GetPrices
    private lateinit var calculateBestTimeSlotUseCase: CalculateBestTimeSlot

    private val strategy = BestTimeSlotCalculationStrategy()

    @Before
    fun setup() {
        priceRepository = PriceRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)
        getPricesUseCase = GetPrices(priceRepository)
        calculateBestTimeSlotUseCase = CalculateBestTimeSlot(strategy)
    }

    @Test
    fun `demonstrate PVPC fixtures usage for realistic price scenarios`() =
        runTest {
            // Arrange - Using realistic price patterns
            coEvery {
                mockLocalDataSource.getPrices(TestDates.TYPICAL_DATE)
            } returns Models.TYPICAL_DAILY_PRICES

            // Act
            val priceResult = getPricesUseCase(TestDates.TYPICAL_DATE)

            // Assert - Verify realistic price structure
            assertTrue(priceResult.isSuccess)
            val prices = priceResult.getOrThrow().prices

            assertEquals(24, prices.size) // Full day coverage
            assertTrue(
                "Night prices should be lower than peak",
                prices[2].pcb < prices[18].pcb,
            ) // 2-3h < 18-19h

            // Verify cheapest hour is during night (02-03h in typical pattern)
            val cheapestPrice = prices.minByOrNull { it.pcb }!!
            assertEquals(2, cheapestPrice.startHour)

            // Verify most expensive hour is during evening peak (18-19h)
            val mostExpensivePrice = prices.maxByOrNull { it.pcb }!!
            assertEquals(18, mostExpensivePrice.startHour)
        }

    @Test
    fun `demonstrate device fixtures usage for appliance optimization`() =
        runTest {
            // Arrange - Using realistic device configurations
            coEvery {
                mockLocalDataSource.getPrices(TestDates.TYPICAL_DATE)
            } returns Models.TYPICAL_DAILY_PRICES

            val washingMachine = CommonDevices.WASHING_MACHINE
            val electricCar = CommonDevices.ELECTRIC_CAR_CHARGER

            // Act - Calculate optimal time slots
            val priceResult = getPricesUseCase(TestDates.TYPICAL_DATE)
            assertTrue(priceResult.isSuccess)
            val prices = priceResult.getOrThrow().prices

            val washingSlot = calculateBestTimeSlotUseCase(washingMachine, prices)
            val carChargingSlot = calculateBestTimeSlotUseCase(electricCar, prices)

            // Assert - Verify realistic optimization behavior
            // Washing machine (2 hours) should prefer early night hours
            assertTrue(
                "Washing machine should start during cheap night hours",
                washingSlot.startHour <= 4,
            )

            // Electric car (8 hours) should use extended night period
            assertTrue(
                "EV charging should start during night hours",
                carChargingSlot.startHour <= 2,
            )
            assertEquals(8, carChargingSlot.endHour - carChargingSlot.startHour)
        }

    @Test
    fun `demonstrate settings fixtures usage for user preferences`() =
        runTest {
            // Arrange - Different user profiles
            val casualUser = UserProfiles.CASUAL_USER
            val professionalUser = UserProfiles.PROFESSIONAL_USER
            val elderlyUser = UserProfiles.ELDERLY_USER

            // Assert - Verify different preference patterns
            // Casual user prefers defaults
            assertEquals(5, casualUser.yAxisSlots)
            assertTrue(
                "Casual user uses system theme",
                casualUser.darkMode.name.contains("SYSTEM"),
            )

            // Professional user prefers detailed, dark interface
            assertTrue(professionalUser.yAxisSlots > casualUser.yAxisSlots)
            assertTrue(
                "Professional uses 24h format",
                professionalUser.timeFormat.name.contains("TWENTY_FOUR"),
            )

            // Elderly user prefers simple, familiar interface
            assertTrue(elderlyUser.yAxisSlots <= casualUser.yAxisSlots)
            assertTrue(
                "Elderly user prefers 12h format",
                elderlyUser.timeFormat.name.contains("TWELVE"),
            )
            assertTrue(
                "Elderly user prefers light mode",
                elderlyUser.darkMode.name.contains("LIGHT"),
            )
        }

    @Test
    fun `demonstrate error scenario fixtures usage`() =
        runTest {
            // Arrange - Network error scenarios
            val networkError = PVPCTestFixtures.Errors.networkException
            coEvery {
                mockLocalDataSource.getPrices(TestDates.TYPICAL_DATE)
            } returns emptyList()
            coEvery {
                mockRemoteDataSource.getPrices(TestDates.TYPICAL_DATE)
            } throws networkError

            // Act
            val result = getPricesUseCase(TestDates.TYPICAL_DATE)

            // Assert - Error handling validation
            assertTrue("Should handle network errors gracefully", result.isFailure)
            assertEquals(networkError, result.exceptionOrNull())
        }

    @Test
    fun `demonstrate edge cases with fixtures`() =
        runTest {
            // Arrange - Edge case: device longer than available price data
            val longDevice = DeviceTestFixtures.EdgeCaseDevices.VERY_LONG_DEVICE // 24 hours
            val limitedPrices = Models.TYPICAL_DAILY_PRICES.take(3) // Only 3 hours of data

            coEvery {
                mockLocalDataSource.getPrices(TestDates.TYPICAL_DATE)
            } returns limitedPrices

            // Act
            val priceResult = getPricesUseCase(TestDates.TYPICAL_DATE)
            assertTrue(priceResult.isSuccess)
            val prices = priceResult.getOrThrow().prices
            val timeSlot = calculateBestTimeSlotUseCase(longDevice, prices)

            // Assert - Verify graceful handling of edge case
            assertTrue(
                "Should handle insufficient price data",
                timeSlot.startHour >= 0,
            )
            assertTrue(
                "End hour should be reasonable",
                timeSlot.endHour >= timeSlot.startHour,
            )
        }
}
