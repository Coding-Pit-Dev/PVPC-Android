package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.models.DarkMode
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val mockGetPrices = mockk<GetPrices>()
    private val mockGetDefaultDate = mockk<GetDefaultDate>()
    private val mockIsValidDate = mockk<IsValidDate>()
    private val mockGetLocalHour = mockk<GetLocalHour>()
    private val mockGetLocalDate = mockk<GetLocalDate>()
    private val mockGetSettings = mockk<GetSettings>()

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val defaultDate = LocalDate.of(2023, 10, 15)
    private val defaultPrices = listOf(
        PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
        PVPCModel("2023-10-15", 1, 2, 0.14, 0.17),
        PVPCModel("2023-10-15", 10, 11, 0.16, 0.19)
    )
    private val defaultSettings = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
        yAxisSlots = 5
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { mockGetDefaultDate() } returns defaultDate
        every { mockGetLocalHour() } returns 10
        every { mockGetLocalDate() } returns defaultDate
        every { mockIsValidDate(any()) } returns true
        coEvery { mockGetPrices(any()) } returns Result.success(defaultPrices)
        every { mockGetSettings() } returns flowOf(defaultSettings)

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        val initialState = viewModel.state.value
        assertTrue(initialState is HomeState.Loading)
    }

    @Test
    fun `state flows to success with correct data`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is HomeState.Success)

        val successState = state as HomeState.Success
        assertEquals("2023-10-15", successState.selectedDate)
        assertEquals("2023-10-15", successState.currentDate)
        assertEquals(3, successState.pvpcEntries.size)
        assertEquals(0.16, successState.currentPrice, 0.001) // Current hour is 10
        assertEquals(10, successState.currentHour)
        assertTrue(successState.nextDateEnabled)
        assertEquals(TimeFormat.TWENTY_FOUR_HOURS, successState.timeFormat)
    }

    @Test
    fun `onPreviewClicked decreases date by one day`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.state.value as HomeState.Success
        assertEquals("2023-10-15", initialState.selectedDate)

        val previousDayPrices = listOf(
            PVPCModel("2023-10-14", 0, 1, 0.12, 0.15),
            PVPCModel("2023-10-14", 10, 11, 0.13, 0.16)
        )
        coEvery { mockGetPrices("2023-10-14") } returns Result.success(previousDayPrices)

        viewModel.onPreviewClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val newState = viewModel.state.value as HomeState.Success
        assertEquals("2023-10-14", newState.selectedDate)
        assertEquals(2, newState.pvpcEntries.size)
    }

    @Test
    fun `onNextClicked increases date by one day`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.state.value as HomeState.Success
        assertEquals("2023-10-15", initialState.selectedDate)

        val nextDayPrices = listOf(
            PVPCModel("2023-10-16", 0, 1, 0.18, 0.21),
            PVPCModel("2023-10-16", 10, 11, 0.19, 0.22)
        )
        coEvery { mockGetPrices("2023-10-16") } returns Result.success(nextDayPrices)

        viewModel.onNextClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val newState = viewModel.state.value as HomeState.Success
        assertEquals("2023-10-16", newState.selectedDate)
        assertEquals(2, newState.pvpcEntries.size)
    }

    @Test
    fun `state handles different time formats`() = runTest {
        val twelveHourSettings = defaultSettings.copy(timeFormat = TimeFormat.TWELVE_HOURS)
        every { mockGetSettings() } returns flowOf(twelveHourSettings)

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as HomeState.Success
        assertEquals(TimeFormat.TWELVE_HOURS, state.timeFormat)
    }

    @Test
    fun `state handles nextDateEnabled based on validation`() = runTest {
        every { mockIsValidDate(defaultDate) } returns false

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as HomeState.Success
        assertFalse(state.nextDateEnabled)
    }

    @Test
    fun `state handles error when prices fail to load`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { mockGetPrices(any()) } returns Result.failure(exception)

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is HomeState.Error)

        val errorState = state as HomeState.Error
        assertEquals("Network error", errorState.error)
    }

    @Test
    fun `state finds current price for current hour`() = runTest {
        val customPrices = listOf(
            PVPCModel("2023-10-15", 8, 9, 0.10, 0.12),
            PVPCModel("2023-10-15", 9, 10, 0.11, 0.13),
            PVPCModel("2023-10-15", 10, 11, 0.20, 0.22), // Current hour
            PVPCModel("2023-10-15", 11, 12, 0.15, 0.17)
        )
        coEvery { mockGetPrices(any()) } returns Result.success(customPrices)
        every { mockGetLocalHour() } returns 10

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as HomeState.Success
        assertEquals(0.20, state.currentPrice, 0.001)
        assertEquals(10, state.currentHour)
    }

    @Test
    fun `state handles empty prices list gracefully`() = runTest {
        coEvery { mockGetPrices(any()) } returns Result.success(emptyList())

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is HomeState.Error) // Should error when trying to find current price
    }

    @Test
    fun `date navigation works correctly across month boundaries`() = runTest {
        every { mockGetDefaultDate() } returns LocalDate.of(2023, 10, 31)
        coEvery { mockGetPrices("2023-10-31") } returns Result.success(defaultPrices)
        coEvery { mockGetPrices("2023-11-01") } returns Result.success(defaultPrices)

        viewModel = HomeViewModel(
            mockGetPrices,
            mockGetDefaultDate,
            mockIsValidDate,
            mockGetLocalHour,
            mockGetLocalDate,
            mockGetSettings
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.state.value as HomeState.Success
        assertEquals("2023-10-31", initialState.selectedDate)

        viewModel.onNextClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val newState = viewModel.state.value as HomeState.Success
        assertEquals("2023-11-01", newState.selectedDate)
    }
}