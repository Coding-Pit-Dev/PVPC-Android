package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.date.GetDefaultDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalHour
import com.codingpit.pvpcplanner.domain.usecase.date.IsValidDate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate

class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupDispatchers() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatchers() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    private val mockGetPrices = mockk<GetPrices>()
    private val mockGetDefaultDate = mockk<GetDefaultDate>()
    private val mockIsValidDate = mockk<IsValidDate>()
    private val mockGetLocalHour = mockk<GetLocalHour>()
    private val mockGetLocalDate = mockk<GetLocalDate>()
    private val mockGetSettings = mockk<GetSettings>()
    private val mockErrorHandler = mockk<ErrorHandler>()
    
    private fun createViewModel(
        prices: Result<List<PVPCModel>> = Result.success(emptyList()),
        currentHour: Int = 10
    ): HomeViewModel {
        val testDate = LocalDate.of(2023, 10, 15)
        val testSettings = Settings(DarkMode.SYSTEM, TimeFormat.TWENTY_FOUR_HOURS)
        
        every { mockGetDefaultDate() } returns testDate
        every { mockIsValidDate(any()) } returns true
        every { mockGetLocalHour() } returns currentHour
        every { mockGetLocalDate() } returns testDate
        every { mockGetSettings() } returns flowOf(testSettings)
        coEvery { mockGetPrices(any()) } returns prices
        
        return HomeViewModel(
            getPrices = mockGetPrices,
            getDefaultDate = mockGetDefaultDate,
            isValidDate = mockIsValidDate,
            getLocalHour = mockGetLocalHour,
            getLocalDate = mockGetLocalDate,
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler
        )
    }

    @Test
    fun `state emits Success when prices are retrieved successfully`() = runTest {
        // Arrange
        val prices = listOf(
            PVPCModel("2023-10-15", 10, 11, 0.15, 0.18),
            PVPCModel("2023-10-15", 11, 12, 0.12, 0.16)
        )
        val viewModel = createViewModel(Result.success(prices), 10)

        // Act
        val state = viewModel.state.first()
        
        // Assert
        assertTrue(state is HomeState.Success)
        val successState = state as HomeState.Success
        assertEquals(prices, successState.pvpcEntries)
        assertEquals(0.15, successState.currentPrice, 0.001)
        assertEquals(10, successState.currentHour)
    }

    @Test
    fun `state emits Error when prices retrieval fails`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        val exception = RuntimeException(errorMessage)
        val viewModel = createViewModel(Result.failure(exception))

        // Act
        val state = viewModel.state.first()
        
        // Assert
        assertTrue(state is HomeState.Error)
        val errorState = state as HomeState.Error
        assertEquals(errorMessage, errorState.error)
    }

    @Test
    fun `state emits Error when getOrThrow fails due to missing data`() = runTest {
        // Arrange
        val prices = emptyList<PVPCModel>()
        val viewModel = createViewModel(Result.success(prices))

        // Act
        val state = viewModel.state.first()
        
        // Assert
        assertTrue(state is HomeState.Error)
        val errorState = state as HomeState.Error
        assertTrue(errorState.error.isNotEmpty())
    }

    @Test
    fun `state emits Error when current hour price is not found`() = runTest {
        // Arrange
        val prices = listOf(
            PVPCModel("2023-10-15", 8, 9, 0.15, 0.18),
            PVPCModel("2023-10-15", 9, 10, 0.12, 0.16)
        )
        val viewModel = createViewModel(Result.success(prices), 11) // Hour not in prices list

        // Act
        val state = viewModel.state.first()
        
        // Assert
        assertTrue(state is HomeState.Error)
    }

    @Test
    fun `onPreviewClicked decrements selected date by one day`() = runTest {
        // Arrange
        val prices = listOf(PVPCModel("2023-10-14", 10, 11, 0.15, 0.18))
        val viewModel = createViewModel(Result.success(prices))
        
        // Get initial state
        val initialState = viewModel.state.first() as HomeState.Success
        val initialDate = initialState.selectedDate

        // Mock the prices for the new date
        coEvery { mockGetPrices("2023-10-14") } returns Result.success(prices)

        // Act
        viewModel.onPreviewClicked()
        val newState = viewModel.state.first() as HomeState.Success

        // Assert
        assertEquals("2023-10-14", newState.selectedDate)
        assertTrue(newState.selectedDate != initialDate)
    }

    @Test
    fun `onNextClicked increments selected date by one day`() = runTest {
        // Arrange
        val prices = listOf(PVPCModel("2023-10-16", 10, 11, 0.15, 0.18))
        val viewModel = createViewModel(Result.success(prices))
        
        // Get initial state
        val initialState = viewModel.state.first() as HomeState.Success
        val initialDate = initialState.selectedDate

        // Mock the prices for the new date
        coEvery { mockGetPrices("2023-10-16") } returns Result.success(prices)

        // Act
        viewModel.onNextClicked()
        val newState = viewModel.state.first() as HomeState.Success

        // Assert
        assertEquals("2023-10-16", newState.selectedDate)
        assertTrue(newState.selectedDate != initialDate)
    }
}