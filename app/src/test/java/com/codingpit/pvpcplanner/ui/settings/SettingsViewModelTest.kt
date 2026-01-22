package com.codingpit.pvpcplanner.ui.settings

import androidx.lifecycle.viewModelScope
import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val mockGetSettings = mockk<GetSettings>()
    private val mockUpdateSetting = mockk<UpdateSetting>()
    private val mockErrorHandler = mockk<ErrorHandler>()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: SettingsViewModel

    private suspend fun <T> TurbineTestContext<T>.awaitFinalState(): T {
        val state = awaitItem()
        return if (state is SettingsState.Loading) awaitItem() else state
    }

    @Before
    fun setup() {
        // Mock UpdateSetting calls
        coEvery { mockUpdateSetting(any<DarkMode>()) } returns Unit
        coEvery { mockUpdateSetting(any<TimeFormat>()) } returns Unit

        // Mock GetSettings to return default settings
        val defaultSettings = Settings(DarkMode.SYSTEM, TimeFormat.TWENTY_FOUR_HOURS)
        every { mockGetSettings() } returns flowOf(defaultSettings)

        // Mock error handler
        every { mockErrorHandler.handleError(any(), any()) } answers {
            val throwable = firstArg<Throwable>()
            ErrorResult.UnknownError(throwable.message ?: "Unknown error")
        }
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `state emits Success when settings are retrieved successfully`() = runTest(testDispatcher) {
        // Arrange
        val settings = Settings(DarkMode.DARK, TimeFormat.TWELVE_HOURS)
        every { mockGetSettings() } returns flowOf(settings)

        // Create new viewModel after mock setup
        viewModel = SettingsViewModel(
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler,
            updateSetting = mockUpdateSetting,
            coroutineDispatcher = testDispatcher
        )

        // Act & Assert
        viewModel.state.test {
            val state = awaitFinalState()
            assertTrue(
                "Expected Success state but got: ${state::class.simpleName}",
                state is SettingsState.Success
            )
            assertEquals(2, (state as SettingsState.Success).settings.size)
        }
        // viewModelScope cancellation handled in tearDown
    }

    @Test
    fun `updateSetting calls correct use case for DarkMode Light`() = runTest(testDispatcher) {
        // Arrange
        val render = SettingRender(
            setting = SettingValue.DarkMode("Dark mode", "Light"),
            options = listOf(
                SettingOption("System", false),
                SettingOption("Light", true),
                SettingOption("Dark", false)
            )
        )
        val option = SettingOption("Light")

        // Act
        viewModel = SettingsViewModel(
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler,
            updateSetting = mockUpdateSetting,
            coroutineDispatcher = testDispatcher
        )
        viewModel.updateSetting(render, option)

        // Assert
        coVerify { mockUpdateSetting(DarkMode.LIGHT) }
    }

    @Test
    fun `updateSetting calls correct use case for DarkMode Dark`() = runTest(testDispatcher) {
        // Arrange
        val render = SettingRender(
            setting = SettingValue.DarkMode("Dark mode", "Dark"),
            options = emptyList()
        )
        val option = SettingOption("Dark")

        // Act
        viewModel = SettingsViewModel(
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler,
            updateSetting = mockUpdateSetting,
            coroutineDispatcher = testDispatcher
        )
        viewModel.updateSetting(render, option)

        // Assert
        coVerify { mockUpdateSetting(DarkMode.DARK) }
    }

    @Test
    fun `updateSetting calls correct use case for DarkMode System (default)`() =
        runTest(testDispatcher) {
            // Arrange
            val render = SettingRender(
                setting = SettingValue.DarkMode("Dark mode", "System"),
                options = emptyList()
            )
            val option = SettingOption("Unknown") // Should default to SYSTEM

            // Act
            viewModel = SettingsViewModel(
                getSettings = mockGetSettings,
                errorHandler = mockErrorHandler,
                updateSetting = mockUpdateSetting,
                coroutineDispatcher = testDispatcher
            )
            viewModel.updateSetting(render, option)

            // Assert
            coVerify { mockUpdateSetting(DarkMode.SYSTEM) }
        }

    @Test
    fun `updateSetting calls correct use case for TimeFormat 24 hours`() = runTest(testDispatcher) {
        // Arrange
        val render = SettingRender(
            setting = SettingValue.TimeFormat("Time format", "24 hours"),
            options = emptyList()
        )
        val option = SettingOption("24 hours")

        // Act
        viewModel = SettingsViewModel(
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler,
            updateSetting = mockUpdateSetting,
            coroutineDispatcher = testDispatcher
        )
        viewModel.updateSetting(render, option)

        // Assert
        coVerify { mockUpdateSetting(TimeFormat.TWENTY_FOUR_HOURS) }
    }

    @Test
    fun `updateSetting calls correct use case for TimeFormat AM PM (default)`() =
        runTest(testDispatcher) {
            // Arrange
            val render = SettingRender(
                setting = SettingValue.TimeFormat("Time format", "AM/PM"),
                options = emptyList()
            )
            val option = SettingOption("Unknown") // Should default to TWELVE_HOURS

            // Act
            viewModel = SettingsViewModel(
                getSettings = mockGetSettings,
                errorHandler = mockErrorHandler,
                updateSetting = mockUpdateSetting,
                coroutineDispatcher = testDispatcher
            )
            viewModel.updateSetting(render, option)

            // Assert
            coVerify { mockUpdateSetting(TimeFormat.TWELVE_HOURS) }
        }

    @Test
    fun `state emits Error when settings retrieval fails`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Network error"
        every { mockGetSettings() } returns flow { throw IOException(errorMessage) }

        viewModel = SettingsViewModel(
            getSettings = mockGetSettings,
            errorHandler = mockErrorHandler,
            updateSetting = mockUpdateSetting,
            coroutineDispatcher = testDispatcher
        )

        // Act & Assert
        viewModel.state.test {
            val finalState = awaitFinalState()
            assertTrue(
                "Expected Error state but got: ${finalState::class.simpleName}",
                finalState is SettingsState.Error
            )
            assertEquals(errorMessage, (finalState as SettingsState.Error).error)
        }
    }
}
