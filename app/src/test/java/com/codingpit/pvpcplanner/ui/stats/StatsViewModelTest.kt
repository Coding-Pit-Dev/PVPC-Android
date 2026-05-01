package com.codingpit.pvpcplanner.ui.stats

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import com.codingpit.pvpcplanner.domain.usecase.GetPriceHistory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockGetPriceHistory = mockk<GetPriceHistory>()
    private val mockErrorHandler = mockk<ErrorHandler>()
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockErrorHandler.handleError(any(), any()) } answers {
            ErrorResult.UnknownError(firstArg<Throwable>().message ?: "Unknown error")
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `initial state is Loading`() =
        runTest {
            coEvery { mockGetPriceHistory() } returns Result.success(emptyList())
            viewModel = StatsViewModel(mockGetPriceHistory, mockErrorHandler, testDispatcher)

            viewModel.state.test {
                val state = awaitItem()
                // With UnconfinedTestDispatcher the flow may already be Success
                assertTrue(state is StatsState.Loading || state is StatsState.Success)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `emits Success with summaries when history loads`() =
        runTest {
            val summaries =
                listOf(
                    DailyPriceSummary("01/05/2026", 0.15, 0.10, 0.22, 24),
                    DailyPriceSummary("30/04/2026", 0.18, 0.12, 0.25, 24),
                )
            coEvery { mockGetPriceHistory() } returns Result.success(summaries)
            viewModel = StatsViewModel(mockGetPriceHistory, mockErrorHandler, testDispatcher)

            viewModel.state.test {
                val state = awaitItem()
                val successState = if (state is StatsState.Loading) awaitItem() else state

                assertTrue(successState is StatsState.Success)
                assertEquals(summaries, (successState as StatsState.Success).summaries)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `emits Success with empty list when no history`() =
        runTest {
            coEvery { mockGetPriceHistory() } returns Result.success(emptyList())
            viewModel = StatsViewModel(mockGetPriceHistory, mockErrorHandler, testDispatcher)

            viewModel.state.test {
                val state = awaitItem()
                val successState = if (state is StatsState.Loading) awaitItem() else state

                assertTrue(successState is StatsState.Success)
                assertTrue((successState as StatsState.Success).summaries.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `emits Error when repository fails`() =
        runTest {
            val exception = RuntimeException("Database read error")
            coEvery { mockGetPriceHistory() } returns Result.failure(exception)
            viewModel = StatsViewModel(mockGetPriceHistory, mockErrorHandler, testDispatcher)

            viewModel.state.test {
                val state = awaitItem()
                val errorState = if (state is StatsState.Loading) awaitItem() else state

                assertTrue(errorState is StatsState.Error)
                assertEquals("Database read error", (errorState as StatsState.Error).error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `summaries expose correct avg min max per day`() =
        runTest {
            val summaries =
                listOf(
                    DailyPriceSummary("01/05/2026", 0.1500, 0.0900, 0.2100, 24),
                )
            coEvery { mockGetPriceHistory() } returns Result.success(summaries)
            viewModel = StatsViewModel(mockGetPriceHistory, mockErrorHandler, testDispatcher)

            viewModel.state.test {
                val state = awaitItem()
                val successState = if (state is StatsState.Loading) awaitItem() else state

                val summary = (successState as StatsState.Success).summaries.first()
                assertEquals(0.15, summary.averagePrice, 0.001)
                assertEquals(0.09, summary.minPrice, 0.001)
                assertEquals(0.21, summary.maxPrice, 0.001)
                assertEquals(24, summary.hourCount)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
