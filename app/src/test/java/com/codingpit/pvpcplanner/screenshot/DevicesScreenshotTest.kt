package com.codingpit.pvpcplanner.screenshot

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.ui.devices.DeviceRender
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesState
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
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
class DevicesScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val washingMachine =
        Device(
            id = 1,
            name = "Lavadora",
            hours = 2,
            icon = "Laundry",
            watts = 1200,
        )

    private val dishwasher =
        Device(
            id = 2,
            name = "Lavavajillas",
            hours = 1,
            icon = "Dishwasher",
            watts = 1800,
        )

    @Test
    fun devicesScreen_emptyState_lightTheme() {
        val mockViewModel = mockk<DevicesViewModel>(relaxed = true)
        every { mockViewModel.state } returns
            MutableStateFlow(
                DevicesState.Success(devicesSlot = emptyList(), searchQuery = ""),
            )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DevicesScreen(viewModel = mockViewModel, onDeviceClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun devicesScreen_withDevices_lightTheme() {
        val mockViewModel = mockk<DevicesViewModel>(relaxed = true)
        every { mockViewModel.state } returns
            MutableStateFlow(
                DevicesState.Success(
                    devicesSlot =
                        listOf(
                            DeviceRender(
                                device = washingMachine,
                                bestSlot = TimeSlot(startHour = 2, endHour = 4),
                                cost = 0.35,
                            ),
                            DeviceRender(
                                device = dishwasher,
                                bestSlot = TimeSlot(startHour = 3, endHour = 4),
                                cost = 0.28,
                            ),
                        ),
                    searchQuery = "",
                ),
            )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DevicesScreen(viewModel = mockViewModel, onDeviceClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun devicesScreen_withDevices_darkTheme() {
        val mockViewModel = mockk<DevicesViewModel>(relaxed = true)
        every { mockViewModel.state } returns
            MutableStateFlow(
                DevicesState.Success(
                    devicesSlot =
                        listOf(
                            DeviceRender(
                                device = washingMachine,
                                bestSlot = TimeSlot(startHour = 2, endHour = 4),
                                cost = 0.35,
                            ),
                        ),
                    searchQuery = "",
                ),
            )

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = true, dynamicColor = false) {
                DevicesScreen(viewModel = mockViewModel, onDeviceClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun devicesScreen_loadingState_lightTheme() {
        val mockViewModel = mockk<DevicesViewModel>(relaxed = true)
        every { mockViewModel.state } returns MutableStateFlow(DevicesState.Loading)

        composeTestRule.setContent {
            PVPCPlannerTheme(darkTheme = false, dynamicColor = false) {
                DevicesScreen(viewModel = mockViewModel, onDeviceClick = {})
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
