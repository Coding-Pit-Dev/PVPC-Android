package com.codingpit.pvpcplanner.ui.settings

import app.cash.turbine.test
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.SchedulePriceAlerts
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    companion object {
        private const val UNKNOWN_LABEL_RES = 0
    }

    private val mockGetSettings = mockk<GetSettings>()
    private val mockUpdateSetting = mockk<UpdateSetting>()
    private val mockErrorHandler = mockk<ErrorHandler>()
    private val mockSchedulePriceAlerts = mockk<SchedulePriceAlerts>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        coEvery { mockUpdateSetting(any<DarkMode>()) } returns Unit
        coEvery { mockUpdateSetting(any<TimeFormat>()) } returns Unit
        coEvery { mockUpdateSetting(any<Float>()) } returns Unit

        every { mockSchedulePriceAlerts(any()) } returns Unit
        every { mockSchedulePriceAlerts.cancel() } returns Unit

        every { mockGetSettings() } returns flowOf(Settings(DarkMode.SYSTEM, TimeFormat.TWELVE_HOURS))

        every { mockErrorHandler.handleError(any(), any()) } answers {
            val throwable = firstArg<Throwable>()
            ErrorResult.UnknownError(throwable.message ?: "Unknown error")
        }
    }

    private fun createViewModel() {
        viewModel =
            SettingsViewModel(
                getSettings = mockGetSettings,
                errorHandler = mockErrorHandler,
                updateSettingUseCase = mockUpdateSetting,
                schedulePriceAlerts = mockSchedulePriceAlerts,
                coroutineDispatcher = testDispatcher,
            )
    }

    @Test
    fun `state emits Success when settings are retrieved successfully`() =
        runTest(testDispatcher) {
            every {
                mockGetSettings()
            } returns flowOf(Settings(DarkMode.DARK, TimeFormat.TWELVE_HOURS, priceThreshold = 0.125f))
            createViewModel()

            viewModel.state.test {
                assertTrue(awaitItem() is SettingsState.Loading)

                val success = awaitItem() as SettingsState.Success
                assertEquals(0.125f, success.priceThreshold)
                assertEquals(2, success.settings.size)
            }
        }

    @Test
    fun `updateSetting calls correct use case for DarkMode Light`() =
        runTest(testDispatcher) {
            createViewModel()
            val render =
                SettingRender(
                    setting = SettingValue.DarkMode(R.string.setting_dark_mode, R.string.option_light),
                    options =
                        listOf(
                            SettingOption(R.string.option_system, false),
                            SettingOption(R.string.option_light, true),
                            SettingOption(R.string.option_dark, false),
                        ),
                )

            viewModel.updateSetting(render, SettingOption(R.string.option_light))
            advanceUntilIdle()

            coVerify(exactly = 1) { mockUpdateSetting(DarkMode.LIGHT) }
        }

    @Test
    fun `updateSetting defaults to 12 hours for unknown option`() =
        runTest(testDispatcher) {
            createViewModel()
            val render =
                SettingRender(
                    setting = SettingValue.TimeFormat(R.string.setting_time_format, R.string.option_ampm),
                    options = emptyList(),
                )

            viewModel.updateSetting(render, SettingOption(UNKNOWN_LABEL_RES))
            advanceUntilIdle()

            coVerify(exactly = 1) { mockUpdateSetting(TimeFormat.TWELVE_HOURS) }
        }

    @Test
    fun `updatePriceThreshold debounces writes and schedules alerts for positive value`() =
        runTest(testDispatcher) {
            createViewModel()

            viewModel.updatePriceThreshold(0.25f)
            advanceTimeBy(499)

            coVerify(exactly = 0) { mockUpdateSetting(any<Float>()) }
            verify(exactly = 0) { mockSchedulePriceAlerts(any()) }
            verify(exactly = 0) { mockSchedulePriceAlerts.cancel() }

            advanceTimeBy(1)
            advanceUntilIdle()

            coVerify(exactly = 1) { mockUpdateSetting(0.25f) }
            verify(exactly = 1) { mockSchedulePriceAlerts(0.25f) }
            verify(exactly = 0) { mockSchedulePriceAlerts.cancel() }
        }

    @Test
    fun `updatePriceThreshold cancels alerts when value is zero`() =
        runTest(testDispatcher) {
            createViewModel()

            viewModel.updatePriceThreshold(0f)
            advanceTimeBy(500)
            advanceUntilIdle()

            coVerify(exactly = 1) { mockUpdateSetting(0f) }
            verify(exactly = 1) { mockSchedulePriceAlerts.cancel() }
            verify(exactly = 0) { mockSchedulePriceAlerts(any()) }
        }

    @Test
    fun `state emits Error when settings retrieval fails`() =
        runTest(testDispatcher) {
            val errorMessage = "Network error"
            every { mockGetSettings() } returns flow { throw IOException(errorMessage) }
            createViewModel()

            viewModel.state.test {
                assertTrue(awaitItem() is SettingsState.Loading)
                val finalState = awaitItem()

                assertTrue(finalState is SettingsState.Error)
                assertEquals(errorMessage, (finalState as SettingsState.Error).error)
            }

            verify(exactly = 1) { mockErrorHandler.handleError(any(), "settings_data") }
        }
}
