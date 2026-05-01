package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPriceHistoryTest {
    private val mockRepository = mockk<PriceRepository>()
    private lateinit var useCase: GetPriceHistory

    @Before
    fun setup() {
        useCase = GetPriceHistory(mockRepository)
    }

    @Test
    fun `returns summaries from repository`() =
        runTest {
            val summaries =
                listOf(
                    DailyPriceSummary("01/05/2026", 0.15, 0.10, 0.22, 24),
                    DailyPriceSummary("30/04/2026", 0.18, 0.12, 0.25, 24),
                )
            coEvery { mockRepository.getPriceHistory() } returns Result.success(summaries)

            val result = useCase()

            assertTrue(result.isSuccess)
            assertEquals(summaries, result.getOrNull())
            coVerify { mockRepository.getPriceHistory() }
        }

    @Test
    fun `returns empty list when no history available`() =
        runTest {
            coEvery { mockRepository.getPriceHistory() } returns Result.success(emptyList())

            val result = useCase()

            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()!!.isEmpty())
        }

    @Test
    fun `propagates repository failure`() =
        runTest {
            val exception = RuntimeException("Database read error")
            coEvery { mockRepository.getPriceHistory() } returns Result.failure(exception)

            val result = useCase()

            assertTrue(result.isFailure)
            assertEquals("Database read error", result.exceptionOrNull()?.message)
        }

    @Test
    fun `returns summaries with correct daily stats`() =
        runTest {
            val summaries =
                listOf(
                    DailyPriceSummary("01/05/2026", 0.1500, 0.0900, 0.2100, 24),
                )
            coEvery { mockRepository.getPriceHistory() } returns Result.success(summaries)

            val result = useCase()
            val summary = result.getOrNull()!!.first()

            assertEquals(0.15, summary.averagePrice, 0.001)
            assertEquals(0.09, summary.minPrice, 0.001)
            assertEquals(0.21, summary.maxPrice, 0.001)
            assertEquals(24, summary.hourCount)
        }
}
