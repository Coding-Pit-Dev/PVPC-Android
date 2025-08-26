package com.codingpit.pvpcplanner.ui.settings

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
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
}
