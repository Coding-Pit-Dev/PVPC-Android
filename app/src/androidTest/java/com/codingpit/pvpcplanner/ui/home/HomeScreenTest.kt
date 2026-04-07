package com.codingpit.pvpcplanner.ui.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val testPrices =
        List(24) { i ->
            PVPCModel(day = "2024-01-15", startHour = i, endHour = i + 1, pcb = 0.1 + i * 0.005, cym = 0.12)
        }

    @Test
    fun homeComponents_displaysPriceValue() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-14",
                    currentPrice = 0.1,
                    currentHour = 0,
                    nextDayEnabled = true,
                )
            }
        }

        val expectedPrice = String.format("%.5f €/kWh", 0.1)
        composeTestRule.onNodeWithText(expectedPrice).assertExists()
    }

    @Test
    fun homeComponents_showsPreviousButton() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-14",
                    currentPrice = 0.1,
                    currentHour = 0,
                    nextDayEnabled = true,
                )
            }
        }

        val previousDayLabel = context.getString(R.string.a11y_previous_day)
        composeTestRule.onNodeWithContentDescription(previousDayLabel).assertExists()
        composeTestRule.onNodeWithContentDescription(previousDayLabel).assertIsEnabled()
    }

    @Test
    fun homeComponents_showsNextButton_whenEnabled() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-14",
                    currentDate = "2024-01-15",
                    currentPrice = 0.1,
                    currentHour = 0,
                    nextDayEnabled = true,
                )
            }
        }

        val nextDayLabel = context.getString(R.string.a11y_next_day)
        composeTestRule.onNodeWithContentDescription(nextDayLabel).assertExists()
        composeTestRule.onNodeWithContentDescription(nextDayLabel).assertIsEnabled()
    }

    @Test
    fun homeComponents_nextButtonDisabled_whenNextDayDisabled() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = "2024-01-15",
                    currentDate = "2024-01-15",
                    currentPrice = 0.1,
                    currentHour = 0,
                    nextDayEnabled = false,
                )
            }
        }

        val nextDayLabel = context.getString(R.string.a11y_next_day)
        composeTestRule.onNodeWithContentDescription(nextDayLabel).assertIsNotEnabled()
    }

    @Test
    fun homeComponents_showsDateLabel() {
        val selectedDate = "2024-01-15"
        composeTestRule.setContent {
            MaterialTheme {
                HomeComponents(
                    pvpcEntries = testPrices,
                    selectedDate = selectedDate,
                    currentDate = "2024-01-14",
                    currentPrice = 0.1,
                    currentHour = 0,
                    nextDayEnabled = true,
                )
            }
        }

        composeTestRule.onNodeWithText(selectedDate).assertExists()
    }
}
