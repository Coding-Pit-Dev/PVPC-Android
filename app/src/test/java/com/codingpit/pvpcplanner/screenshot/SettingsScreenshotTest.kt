package com.codingpit.pvpcplanner.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.ui.settings.SettingOption
import com.codingpit.pvpcplanner.ui.settings.SettingRender
import com.codingpit.pvpcplanner.ui.settings.SettingValue
import com.codingpit.pvpcplanner.ui.settings.SettingsScreen
import com.codingpit.pvpcplanner.ui.settings.SettingsState
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.ui.theme.PVPCPlannerTheme
import com.github.takahirom.roborazzi.captureRoboImage
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-mdpi")
class SettingsScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultSettings =
        listOf(
            SettingRender(
                setting =
                    SettingValue.DarkMode(
                        titleRes = R.string.setting_dark_mode,
                        valueRes = R.string.option_system,
                    ),
                options =
                    listOf(
                        SettingOption(labelRes = R.string.option_system, selected = true),
                        SettingOption(labelRes = R.string.option_light, selected = false),
                        SettingOption(labelRes = R.string.option_dark, selected = false),
                    ),
            ),
            SettingRender(
                setting =
                    SettingValue.TimeFormat(
                        titleRes = R.string.setting_time_format,
                        valueRes = R.string.option_24h,
                    ),
                options =
                    listOf(
                        SettingOption(labelRes = R.string.option_24h, selected = true),
                        SettingOption(labelRes = R.string.option_ampm, selected = false),
                    ),
            ),
        )

    @Test
    fun settingsScreen_success_lightTheme() {
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        every { mockViewModel.state } returns
            MutableStateFlow(
                SettingsState.Success(settings = defaultSettings),
            )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                SettingsScreen(viewModel = mockViewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun settingsScreen_success_darkTheme() {
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        every { mockViewModel.state } returns
            MutableStateFlow(
                SettingsState.Success(settings = defaultSettings),
            )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = true, dynamicColor = false) {
                SettingsScreen(viewModel = mockViewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun settingsScreen_loading_lightTheme() {
        val mockViewModel = mockk<SettingsViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(SettingsState.Loading)

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                SettingsScreen(viewModel = mockViewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
