package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.fixtures.PVPCTestFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PriceRepositoryImplTest {
    private val mockRemoteDataSource = mockk<RemoteDataSource>()
    private val mockLocalDataSource = mockk<PriceLocalDataSource>()
    private lateinit var repository: PriceRepositoryImpl

    @Before
    fun setup() {
        repository = PriceRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun `getPrices returns local data when available`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val localPrices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                    PVPCModel("2023-10-15", 1, 2, 0.14, 0.17),
                )
            coEvery { mockLocalDataSource.getPrices(date) } returns localPrices

            // Act
            val result = repository.getPrices(date)

            // Assert
            assertTrue(result.isSuccess)
            val fetchResult = result.getOrNull()
            assertEquals(localPrices, fetchResult?.prices)
            assertEquals(true, fetchResult?.isFromCache)
            coVerify { mockLocalDataSource.getPrices(date) }
            coVerify(exactly = 0) { mockRemoteDataSource.getPrices(any()) }
        }

    @Test
    fun `getPrices fetches from remote and saves locally when local is empty`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val remotePrices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                    PVPCModel("2023-10-15", 1, 2, 0.14, 0.17),
                )
            coEvery { mockLocalDataSource.getPrices(date) } returns emptyList()
            coEvery { mockRemoteDataSource.getPrices(date) } returns remotePrices
            coEvery { mockLocalDataSource.savePrices(remotePrices) } returns Unit

            // Act
            val result = repository.getPrices(date)

            // Assert
            assertTrue(result.isSuccess)
            val fetchResult = result.getOrNull()
            assertEquals(remotePrices, fetchResult?.prices)
            assertEquals(false, fetchResult?.isFromCache)
            coVerify { mockLocalDataSource.getPrices(date) }
            coVerify { mockRemoteDataSource.getPrices(date) }
            coVerify { mockLocalDataSource.savePrices(remotePrices) }
        }

    @Test
    fun `getPrices returns failure when remote throws exception`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val exception = RuntimeException("Network error")
            coEvery { mockLocalDataSource.getPrices(date) } returns emptyList()
            coEvery { mockRemoteDataSource.getPrices(date) } throws exception

            // Act
            val result = repository.getPrices(date)

            // Assert
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify { mockLocalDataSource.getPrices(date) }
            coVerify { mockRemoteDataSource.getPrices(date) }
            coVerify(exactly = 0) { mockLocalDataSource.savePrices(any()) }
        }

    @Test
    fun `getPrices returns failure when local throws exception during initial check`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val exception = RuntimeException("Database error")
            coEvery { mockLocalDataSource.getPrices(date) } throws exception

            // Act
            val result = repository.getPrices(date)

            // Assert
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify { mockLocalDataSource.getPrices(date) }
            coVerify(exactly = 0) { mockRemoteDataSource.getPrices(any()) }
        }

    @Test
    fun `getPriceHistory returns days in chronological descending order`() =
        runTest {
            // Arrange
            every { mockLocalDataSource.getAvailableDays() } returns PVPCTestFixtures.Models.HISTORY_DAYS_UNSORTED
            every { mockLocalDataSource.getPricesByStoredDay("31/01/2026") } returns
                PVPCTestFixtures.Models.HISTORY_PRICES_2026_01_31
            every { mockLocalDataSource.getPricesByStoredDay("01/02/2026") } returns
                PVPCTestFixtures.Models.HISTORY_PRICES_2026_02_01
            every { mockLocalDataSource.getPricesByStoredDay("15/12/2025") } returns
                PVPCTestFixtures.Models.HISTORY_PRICES_2025_12_15

            // Act
            val result = repository.getPriceHistory()

            // Assert
            assertTrue(result.isSuccess)
            val summaries = result.getOrThrow()
            assertEquals(
                listOf("01/02/2026", "31/01/2026", "15/12/2025"),
                summaries.map(DailyPriceSummary::day),
            )
            assertEquals(0.125, summaries[0].averagePrice, 0.0001)
            assertEquals(0.10, summaries[0].minPrice, 0.0001)
            assertEquals(0.15, summaries[0].maxPrice, 0.0001)
            assertEquals(2, summaries[0].hourCount)
        }

    @Test
    fun `getPriceHistory returns failure when local history lookup fails`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Database error")
            every { mockLocalDataSource.getAvailableDays() } throws exception

            // Act
            val result = repository.getPriceHistory()

            // Assert
            assertTrue(result.isFailure)
            assertEquals("Database error", result.exceptionOrNull()?.message)
        }
}
