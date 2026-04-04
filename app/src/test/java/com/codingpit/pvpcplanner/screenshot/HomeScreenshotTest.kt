package com.codingpit.pvpcplanner.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.ui.home.HomeComponents
import com.codingpit.pvpcplanner.ui.theme.PVPCPlannerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-mdpi")
class HomeScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testPrices = List(24) { i ->
        PVPCModel(
            day = "2024-01-15",
            startHour = i,
            endHour = i + 1,
            pcb = 0.05 + i * 0.005,
            cym = 0.06,
        )
    }

    @Test
    fun homeScreen_success_lightTheme() {
        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-15",
                    currentPrice = 0.10,
                    currentHour = 12,
                    nextDayEnabled = false,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun homeScreen_success_darkTheme() {
        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = true, dynamicColor = false) {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-15",
                    currentPrice = 0.10,
                    currentHour = 12,
                    nextDayEnabled = false,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun homeScreen_emptyPrices_lightTheme() {
        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                HomeComponents(
                    pvpcEntries = emptyList(),
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-15",
                    currentPrice = 0.0,
                    currentHour = 0,
                    nextDayEnabled = true,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
