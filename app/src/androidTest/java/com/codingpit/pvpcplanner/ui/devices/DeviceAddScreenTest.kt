package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.DeviceCategory
import com.codingpit.pvpcplanner.utils.CategoryUiModel
import com.codingpit.pvpcplanner.utils.DeviceIcon
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceAddScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val testIcon = DeviceIcon(id = "Default", icon = Icons.Default.Power, labelRes = R.string.device_default)
    private val testCategory = CategoryUiModel(id = DeviceCategory.APPLIANCES, labelRes = R.string.category_appliances)

    private fun buildSuccessState(
        isEditMode: Boolean = false,
        deviceName: String = "Test Device",
        watts: String = "500",
        hours: String = "2",
        saveEnabled: Boolean = true,
        hoursError: String? = null,
    ) = DeviceAddState.Success(
        isEditMode = isEditMode,
        deviceName = deviceName,
        watts = watts,
        hours = hours,
        saveEnabled = saveEnabled,
        hoursError = hoursError,
        selectedIcon = testIcon,
        availableIcons = listOf(testIcon),
        selectedCategory = testCategory,
        availableCategories = listOf(testCategory),
    )

    @Test
    fun deviceAddScreen_showsAddTitle_whenNotEditMode() {
        val mockVM = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(buildSuccessState(isEditMode = false))

        composeTestRule.setContent {
            MaterialTheme {
                DeviceAddScreen(viewModel = mockVM, onBackClick = {})
            }
        }

        val addTitle = context.getString(R.string.add_device_title)
        composeTestRule.onNodeWithText(addTitle).assertExists()
    }

    @Test
    fun deviceAddScreen_saveButtonDisabled_whenNameEmpty() {
        val mockVM = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockVM.state } returns
            MutableStateFlow(
                buildSuccessState(deviceName = "", saveEnabled = false),
            )

        composeTestRule.setContent {
            MaterialTheme {
                DeviceAddScreen(viewModel = mockVM, onBackClick = {})
            }
        }

        val saveButtonText = context.getString(R.string.save_device)
        composeTestRule.onNodeWithText(saveButtonText).assertIsNotEnabled()
    }

    @Test
    fun deviceAddScreen_saveButtonEnabled_whenFormValid() {
        val mockVM = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockVM.state } returns
            MutableStateFlow(
                buildSuccessState(
                    deviceName = "Washing Machine",
                    watts = "2000",
                    hours = "2",
                    saveEnabled = true,
                ),
            )

        composeTestRule.setContent {
            MaterialTheme {
                DeviceAddScreen(viewModel = mockVM, onBackClick = {})
            }
        }

        val saveButtonText = context.getString(R.string.save_device)
        composeTestRule.onNodeWithText(saveButtonText).assertIsEnabled()
    }

    @Test
    fun deviceAddScreen_showsHoursError_whenHoursExceed24() {
        val mockVM = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockVM.state } returns
            MutableStateFlow(
                buildSuccessState(
                    hours = "25",
                    saveEnabled = false,
                    hoursError = "error_validation_device_hours_max",
                ),
            )

        composeTestRule.setContent {
            MaterialTheme {
                DeviceAddScreen(viewModel = mockVM, onBackClick = {})
            }
        }

        val errorText = context.getString(R.string.error_validation_device_hours_max)
        composeTestRule.onNodeWithText(errorText).assertExists()
    }
}
