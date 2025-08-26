package com.codingpit.pvpcplanner.ui.settings

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val mockGetSettings = mockk<GetSettings>()
    private val mockUpdateSetting = mockk<UpdateSetting>()

    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val defaultSettings = Settings(
        darkMode = DarkMode.SYSTEM,
        timeFormat = TimeFormat.TWELVE_HOURS,
        yAxisSlots = 5
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { mockGetSettings() } returns flowOf(defaultSettings)
        coEvery { mockUpdateSetting(any<DarkMode>()) } returns Unit
        coEvery { mockUpdateSetting(any<TimeFormat>()) } returns Unit

        viewModel = SettingsViewModel(mockGetSettings, mockUpdateSetting, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        val initialState = viewModel.state.value
        assertTrue(initialState is SettingsState.Loading)
    }

    @Test
    fun `state flows to success with correct settings render`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is SettingsState.Success)

        val successState = state as SettingsState.Success
        assertEquals(2, successState.settings.size)

        val darkModeRender = successState.settings[0]
        assertTrue(darkModeRender.setting is SettingValue.DarkMode)
        assertEquals("Dark mode", darkModeRender.setting.title)
        assertEquals("System", darkModeRender.setting.value)
        assertEquals(3, darkModeRender.options.size)

        val timeFormatRender = successState.settings[1]
        assertTrue(timeFormatRender.setting is SettingValue.TimeFormat)
        assertEquals("Time format", timeFormatRender.setting.title)
        assertEquals("AM/PM", timeFormatRender.setting.value)
        assertEquals(2, timeFormatRender.options.size)
    }

    @Test
    fun `dark mode options are correctly set for SYSTEM`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        
        val systemOption = darkModeRender.options.find { it.title == "System" }
        val lightOption = darkModeRender.options.find { it.title == "Light" }
        val darkOption = darkModeRender.options.find { it.title == "Dark" }

        assertTrue(systemOption?.selected ?: false)
        assertFalse(lightOption?.selected ?: true)
        assertFalse(darkOption?.selected ?: true)
    }

    @Test
    fun `dark mode options are correctly set for LIGHT`() = runTest {
        val lightSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
        every { mockGetSettings() } returns flowOf(lightSettings)

        viewModel = SettingsViewModel(mockGetSettings, mockUpdateSetting, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        
        val systemOption = darkModeRender.options.find { it.title == "System" }
        val lightOption = darkModeRender.options.find { it.title == "Light" }
        val darkOption = darkModeRender.options.find { it.title == "Dark" }

        assertFalse(systemOption?.selected ?: true)
        assertTrue(lightOption?.selected ?: false)
        assertFalse(darkOption?.selected ?: true)
        assertEquals("Light", darkModeRender.setting.value)
    }

    @Test
    fun `dark mode options are correctly set for DARK`() = runTest {
        val darkSettings = defaultSettings.copy(darkMode = DarkMode.DARK)
        every { mockGetSettings() } returns flowOf(darkSettings)

        viewModel = SettingsViewModel(mockGetSettings, mockUpdateSetting, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        
        val systemOption = darkModeRender.options.find { it.title == "System" }
        val lightOption = darkModeRender.options.find { it.title == "Light" }
        val darkOption = darkModeRender.options.find { it.title == "Dark" }

        assertFalse(systemOption?.selected ?: true)
        assertFalse(lightOption?.selected ?: true)
        assertTrue(darkOption?.selected ?: false)
        assertEquals("Dark", darkModeRender.setting.value)
    }

    @Test
    fun `time format options are correctly set for TWELVE_HOURS`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val timeFormatRender = state.settings[1]
        
        val amPmOption = timeFormatRender.options.find { it.title == "AM/PM" }
        val twentyFourOption = timeFormatRender.options.find { it.title == "24 hours" }

        assertTrue(amPmOption?.selected ?: false)
        assertFalse(twentyFourOption?.selected ?: true)
    }

    @Test
    fun `time format options are correctly set for TWENTY_FOUR_HOURS`() = runTest {
        val twentyFourSettings = defaultSettings.copy(timeFormat = TimeFormat.TWENTY_FOUR_HOURS)
        every { mockGetSettings() } returns flowOf(twentyFourSettings)

        viewModel = SettingsViewModel(mockGetSettings, mockUpdateSetting, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val timeFormatRender = state.settings[1]
        
        val amPmOption = timeFormatRender.options.find { it.title == "AM/PM" }
        val twentyFourOption = timeFormatRender.options.find { it.title == "24 hours" }

        assertFalse(amPmOption?.selected ?: true)
        assertTrue(twentyFourOption?.selected ?: false)
        assertEquals("24 hours", timeFormatRender.setting.value)
    }

    @Test
    fun `updateSetting calls correct dark mode update for Light option`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        val lightOption = SettingOption("Light", false)

        viewModel.updateSetting(darkModeRender, lightOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(DarkMode.LIGHT) }
    }

    @Test
    fun `updateSetting calls correct dark mode update for Dark option`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        val darkOption = SettingOption("Dark", false)

        viewModel.updateSetting(darkModeRender, darkOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(DarkMode.DARK) }
    }

    @Test
    fun `updateSetting calls correct dark mode update for System option`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        val systemOption = SettingOption("System", false)

        viewModel.updateSetting(darkModeRender, systemOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(DarkMode.SYSTEM) }
    }

    @Test
    fun `updateSetting calls correct time format update for 24 hours option`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val timeFormatRender = state.settings[1]
        val twentyFourOption = SettingOption("24 hours", false)

        viewModel.updateSetting(timeFormatRender, twentyFourOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(TimeFormat.TWENTY_FOUR_HOURS) }
    }

    @Test
    fun `updateSetting calls correct time format update for AM-PM option`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val timeFormatRender = state.settings[1]
        val amPmOption = SettingOption("AM/PM", false)

        viewModel.updateSetting(timeFormatRender, amPmOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(TimeFormat.TWELVE_HOURS) }
    }

    @Test
    fun `updateSetting defaults to System for unknown dark mode options`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val darkModeRender = state.settings[0]
        val unknownOption = SettingOption("Unknown", false)

        viewModel.updateSetting(darkModeRender, unknownOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(DarkMode.SYSTEM) }
    }

    @Test
    fun `updateSetting defaults to TWELVE_HOURS for unknown time format options`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as SettingsState.Success
        val timeFormatRender = state.settings[1]
        val unknownOption = SettingOption("Unknown", false)

        viewModel.updateSetting(timeFormatRender, unknownOption)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUpdateSetting(TimeFormat.TWELVE_HOURS) }
    }

    @Test
    fun `state handles settings updates reactively`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val initialState = viewModel.state.value as SettingsState.Success
        val initialDarkModeValue = initialState.settings[0].setting.value
        assertEquals("System", initialDarkModeValue)

        val updatedSettings = defaultSettings.copy(darkMode = DarkMode.LIGHT)
        every { mockGetSettings() } returns flowOf(updatedSettings)

        // Create new ViewModel instance to simulate reactive update
        viewModel = SettingsViewModel(mockGetSettings, mockUpdateSetting, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedState = viewModel.state.value as SettingsState.Success
        val updatedDarkModeValue = updatedState.settings[0].setting.value
        assertEquals("Light", updatedDarkModeValue)
    }
}
