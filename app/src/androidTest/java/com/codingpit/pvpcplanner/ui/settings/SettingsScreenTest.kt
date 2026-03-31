package com.codingpit.pvpcplanner.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.codingpit.pvpcplanner.R
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun buildSettingsSuccess(): SettingsState.Success {
        val darkModeSetting = SettingRender(
            setting = SettingValue.DarkMode(
                titleRes = R.string.setting_dark_mode,
                valueRes = R.string.option_system,
            ),
            options = listOf(
                SettingOption(labelRes = R.string.option_system, selected = true),
                SettingOption(labelRes = R.string.option_light, selected = false),
                SettingOption(labelRes = R.string.option_dark, selected = false),
            ),
        )
        val timeFormatSetting = SettingRender(
            setting = SettingValue.TimeFormat(
                titleRes = R.string.setting_time_format,
                valueRes = R.string.option_24h,
            ),
            options = listOf(
                SettingOption(labelRes = R.string.option_24h, selected = true),
                SettingOption(labelRes = R.string.option_ampm, selected = false),
            ),
        )
        return SettingsState.Success(settings = listOf(darkModeSetting, timeFormatSetting))
    }

    @Test
    fun settingsScreen_showsLoadingState() {
        val mockVM = mockk<SettingsViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(SettingsState.Loading)

        composeTestRule.setContent {
            MaterialTheme {
                SettingsScreen(viewModel = mockVM)
            }
        }

        // Loading state renders an empty composable; verify settings items are not shown
        val darkModeTitle = context.getString(R.string.setting_dark_mode)
        composeTestRule.onNodeWithText(darkModeTitle).assertDoesNotExist()
    }

    @Test
    fun settingsScreen_showsSettingItems() {
        val mockVM = mockk<SettingsViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(buildSettingsSuccess())

        composeTestRule.setContent {
            MaterialTheme {
                SettingsScreen(viewModel = mockVM)
            }
        }

        val darkModeTitle = context.getString(R.string.setting_dark_mode)
        val timeFormatTitle = context.getString(R.string.setting_time_format)

        composeTestRule.onNodeWithText(darkModeTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(timeFormatTitle).assertIsDisplayed()
    }

    @Test
    fun settingsScreen_showsOptionsBottomSheet_whenSettingTapped() {
        val mockVM = mockk<SettingsViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(buildSettingsSuccess())

        composeTestRule.setContent {
            MaterialTheme {
                SettingsScreen(viewModel = mockVM)
            }
        }

        val darkModeTitle = context.getString(R.string.setting_dark_mode)
        composeTestRule.onNodeWithText(darkModeTitle).performClick()

        // After tapping, the bottom sheet with options should appear
        val optionLight = context.getString(R.string.option_light)
        composeTestRule.onNodeWithText(optionLight).assertIsDisplayed()
    }
}
