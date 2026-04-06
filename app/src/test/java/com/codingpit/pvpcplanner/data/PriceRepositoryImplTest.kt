package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.models.PVPCModel
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
            assertEquals(localPrices, result.getOrNull())
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
            assertEquals(remotePrices, result.getOrNull())
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
            val unsortedDays = listOf("31/01/2026", "01/02/2026", "15/12/2025")
            every { mockLocalDataSource.getAvailableDays() } returns unsortedDays
            every { mockLocalDataSource.getPricesByStoredDay("31/01/2026") } returns listOf(
                PVPCModel("31/01/2026", 0, 1, 0.30, 0.30),
                PVPCModel("31/01/2026", 1, 2, 0.20, 0.20),
            )
            every { mockLocalDataSource.getPricesByStoredDay("01/02/2026") } returns listOf(
                PVPCModel("01/02/2026", 0, 1, 0.10, 0.10),
                PVPCModel("01/02/2026", 1, 2, 0.15, 0.15),
            )
            every { mockLocalDataSource.getPricesByStoredDay("15/12/2025") } returns listOf(
                PVPCModel("15/12/2025", 0, 1, 0.40, 0.40),
            )

            // Act
            val result = repository.getPriceHistory()

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(
                listOf("01/02/2026", "31/01/2026", "15/12/2025"),
                result.getOrThrow().map(DailyPriceSummary::day),
            )
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
