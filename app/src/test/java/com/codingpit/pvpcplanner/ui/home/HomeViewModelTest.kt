package com.codingpit.pvpcplanner.ui.home

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.date.GetDefaultDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalHour
import com.codingpit.pvpcplanner.domain.usecase.date.IsValidDate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setupDispatchers() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    private val mockGetPrices = mockk<GetPrices>()
    private val mockGetDefaultDate = mockk<GetDefaultDate>()
    private val mockIsValidDate = mockk<IsValidDate>()
    private val mockGetLocalHour = mockk<GetLocalHour>()
    private val mockGetLocalDate = mockk<GetLocalDate>()
    private val mockGetSettings = mockk<GetSettings>()
    private val mockErrorHandler = mockk<ErrorHandler>()

    private fun createViewModel(
        prices: Result<PriceFetchResult> = Result.success(PriceFetchResult(emptyList(), isFromCache = false)),
        currentHour: Int = 10,
    ): HomeViewModel {
        val testDate = LocalDate.of(2023, 10, 15)
        val testSettings = Settings(DarkMode.SYSTEM, TimeFormat.TWENTY_FOUR_HOURS)

        every { mockGetDefaultDate() } returns testDate
        every { mockIsValidDate(any()) } returns true
        every { mockGetLocalHour() } returns currentHour
        every { mockGetLocalDate() } returns testDate
        every { mockGetSettings() } returns flowOf(testSettings)
        coEvery { mockGetPrices(any()) } returns prices

        // Mock error handler
        every { mockErrorHandler.handleError(any(), any()) } answers {
            val throwable = firstArg<Throwable>()
            com.codingpit.pvpcplanner.domain.error.ErrorResult.UnknownError(
                throwable.message ?: "Unknown error",
            )
        }

        val useCaseProvider =
            HomeUseCaseProvider(
                getPrices = mockGetPrices,
                getDefaultDate = mockGetDefaultDate,
                isValidDate = mockIsValidDate,
                getLocalHour = mockGetLocalHour,
                getLocalDate = mockGetLocalDate,
                getSettings = mockGetSettings,
            )

        viewModel =
            HomeViewModel(
                useCaseProvider = useCaseProvider,
                errorHandler = mockErrorHandler,
                coroutineDispatcher = testDispatcher,
            )
        return viewModel
    }

    @Test
    fun `state emits Success when prices are retrieved successfully`() =
        runTest {
            // Arrange
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 10, 11, 0.15, 0.18),
                    PVPCModel("2023-10-15", 11, 12, 0.12, 0.16),
                )
            val viewModel = createViewModel(Result.success(PriceFetchResult(prices, isFromCache = false)), 10)

            // Act & Assert
            viewModel.state.test {
                // Unconfined dispatcher might have already emitted Loading and Success
                // StateFlow replay=1 gives the latest value.
                val state = awaitItem()
                val successState = if (state is HomeState.Loading) awaitItem() else state

                assertTrue(successState is HomeState.Success)
                val success = successState as HomeState.Success
                assertEquals(prices, success.pvpcEntries)
                assertEquals(0.15, success.currentPrice, 0.001)
                assertEquals(10, success.currentHour)
            }
        }

    @Test
    fun `state emits Success with isFromCache true when data is from cache`() =
        runTest {
            // Arrange
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 10, 11, 0.15, 0.18),
                )
            val viewModel = createViewModel(Result.success(PriceFetchResult(prices, isFromCache = true)), 10)

            // Act & Assert
            viewModel.state.test {
                val state = awaitItem()
                val successState = if (state is HomeState.Loading) awaitItem() else state

                assertTrue(successState is HomeState.Success)
                assertTrue((successState as HomeState.Success).isFromCache)
            }
        }

    @Test
    fun `state emits Error when prices retrieval fails`() =
        runTest {
            // Arrange
            val errorMessage = "Network error"
            val exception = RuntimeException(errorMessage)
            val viewModel = createViewModel(Result.failure(exception))

            // Act & Assert
            viewModel.state.test {
                val state = awaitItem()
                val errorState = if (state is HomeState.Loading) awaitItem() else state

                assertTrue(errorState is HomeState.Error)
                assertEquals(errorMessage, (errorState as HomeState.Error).error)
            }
        }

    @Test
    fun `state emits Error when getOrThrow fails due to missing data`() =
        runTest {
            // Arrange
            val viewModel = createViewModel(Result.success(PriceFetchResult(emptyList(), isFromCache = false)))

            // Act & Assert
            viewModel.state.test {
                val state = awaitItem()
                val errorState = if (state is HomeState.Loading) awaitItem() else state

                assertTrue(errorState is HomeState.Error)
                assertTrue((errorState as HomeState.Error).error.isNotEmpty())
            }
        }

    @Test
    fun `state emits Error when current hour price is not found`() =
        runTest {
            // Arrange
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 8, 9, 0.15, 0.18),
                    PVPCModel("2023-10-15", 9, 10, 0.12, 0.16),
                )
            val viewModel = createViewModel(Result.success(PriceFetchResult(prices, isFromCache = false)), 11)

            // Act & Assert
            viewModel.state.test {
                val state = awaitItem()
                val errorState = if (state is HomeState.Loading) awaitItem() else state

                assertTrue(errorState is HomeState.Error)
            }
        }

    @Test
    fun `onPreviousClicked decrements selected date by one day`() =
        runTest {
            // Arrange
            val prices = listOf(PVPCModel("2023-10-14", 10, 11, 0.15, 0.18))
            val viewModel = createViewModel(Result.success(PriceFetchResult(prices, isFromCache = false)))

            // Mock the prices for the new date
            coEvery { mockGetPrices("2023-10-14") } returns Result.success(PriceFetchResult(prices, isFromCache = false))

            // Act & Assert
            viewModel.state.test {
                // Initial state (likely Success or Loading then Success)
                var state = awaitItem()
                if (state is HomeState.Loading) {
                    state = awaitItem()
                }
                assertTrue(state is HomeState.Success)

                // Act
                viewModel.onPreviousClicked()

                // Should emit Loading then Success or just Success depending on speed/dispatcher
                state = awaitItem()
                if (state is HomeState.Loading) {
                    state = awaitItem()
                }

                assertTrue(state is HomeState.Success)
                assertEquals("2023-10-14", (state as HomeState.Success).selectedDate)
            }
        }

    @Test
    fun `onNextClicked increments selected date by one day`() =
        runTest {
            // Arrange
            val prices = listOf(PVPCModel("2023-10-16", 10, 11, 0.15, 0.18))
            val viewModel = createViewModel(Result.success(PriceFetchResult(prices, isFromCache = false)))

            // Mock the prices for the new date
            coEvery { mockGetPrices("2023-10-16") } returns Result.success(PriceFetchResult(prices, isFromCache = false))

            // Act & Assert
            viewModel.state.test {
                // Initial state
                var state = awaitItem()
                if (state is HomeState.Loading) {
                    state = awaitItem()
                }
                assertTrue(state is HomeState.Success)

                // Act
                viewModel.onNextClicked()

                // New state
                state = awaitItem()
                if (state is HomeState.Loading) {
                    state = awaitItem()
                }

                assertTrue(state is HomeState.Success)
                assertEquals("2023-10-16", (state as HomeState.Success).selectedDate)
            }
        }
}
