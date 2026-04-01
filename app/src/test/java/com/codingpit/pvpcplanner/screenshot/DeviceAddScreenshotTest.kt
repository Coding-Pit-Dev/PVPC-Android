package com.codingpit.pvpcplanner.screenshot

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Power
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.DeviceCategory
import com.codingpit.pvpcplanner.ui.devices.DeviceAddScreen
import com.codingpit.pvpcplanner.ui.devices.DeviceAddState
import com.codingpit.pvpcplanner.ui.devices.DeviceAddViewModel
import com.codingpit.pvpcplanner.ui.theme.PVPCPlannerTheme
import com.codingpit.pvpcplanner.utils.CategoryUiModel
import com.codingpit.pvpcplanner.utils.DeviceIcon
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
class DeviceAddScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultIcon = DeviceIcon(
        id = "Default",
        icon = Icons.Default.Power,
        labelRes = R.string.device_default,
    )

    private val laundryIcon = DeviceIcon(
        id = "Laundry",
        icon = Icons.Default.LocalLaundryService,
        labelRes = R.string.device_laundry,
    )

    private val appliancesCategory = CategoryUiModel(
        id = DeviceCategory.APPLIANCES,
        labelRes = R.string.category_appliances,
    )

    @Test
    fun deviceAddScreen_emptyForm_lightTheme() {
        val mockViewModel = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(
            DeviceAddState.Success(
                selectedIcon = defaultIcon,
                availableIcons = listOf(defaultIcon, laundryIcon),
                selectedCategory = appliancesCategory,
                availableCategories = listOf(appliancesCategory),
                saveEnabled = false,
            ),
        )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DeviceAddScreen(viewModel = mockViewModel, device = null, onBackClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun deviceAddScreen_filledForm_lightTheme() {
        val mockViewModel = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(
            DeviceAddState.Success(
                deviceName = "Lavadora",
                watts = "1200",
                hours = "2",
                selectedIcon = laundryIcon,
                availableIcons = listOf(defaultIcon, laundryIcon),
                selectedCategory = appliancesCategory,
                availableCategories = listOf(appliancesCategory),
                saveEnabled = true,
            ),
        )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DeviceAddScreen(viewModel = mockViewModel, device = null, onBackClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun deviceAddScreen_editMode_lightTheme() {
        val mockViewModel = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(
            DeviceAddState.Success(
                deviceId = 1,
                isEditMode = true,
                deviceName = "Lavadora Samsung",
                watts = "1500",
                hours = "2",
                selectedIcon = laundryIcon,
                availableIcons = listOf(defaultIcon, laundryIcon),
                selectedCategory = appliancesCategory,
                availableCategories = listOf(appliancesCategory),
                saveEnabled = true,
            ),
        )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DeviceAddScreen(viewModel = mockViewModel, device = null, onBackClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun deviceAddScreen_darkTheme() {
        val mockViewModel = mockk<DeviceAddViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(
            DeviceAddState.Success(
                deviceName = "Lavavajillas",
                watts = "1800",
                hours = "1",
                selectedIcon = defaultIcon,
                availableIcons = listOf(defaultIcon),
                selectedCategory = appliancesCategory,
                availableCategories = listOf(appliancesCategory),
                saveEnabled = true,
            ),
        )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = true, dynamicColor = false) {
                DeviceAddScreen(viewModel = mockViewModel, device = null, onBackClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
