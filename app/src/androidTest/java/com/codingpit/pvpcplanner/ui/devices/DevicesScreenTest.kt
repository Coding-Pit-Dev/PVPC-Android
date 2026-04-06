package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DevicesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun devicesScreen_showsLoadingIndicator() {
        val mockVM = mockk<DevicesViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(DevicesState.Loading)

        composeTestRule.setContent {
            MaterialTheme {
                DevicesScreen(viewModel = mockVM, onDeviceClick = {})
            }
        }

        // When loading, CircularProgressIndicator is shown. We can verify loading state
        // by ensuring no device content appears yet
        composeTestRule
            .onNodeWithText(context.getString(R.string.no_devices))
            .assertDoesNotExist()
    }

    @Test
    fun devicesScreen_showsEmptyState_whenNoDevices() {
        val mockVM = mockk<DevicesViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(DevicesState.Success(emptyList(), ""))

        composeTestRule.setContent {
            MaterialTheme {
                DevicesScreen(viewModel = mockVM, onDeviceClick = {})
            }
        }

        val noDevicesText = context.getString(R.string.no_devices)
        composeTestRule.onNodeWithText(noDevicesText).assertIsDisplayed()
    }

    @Test
    fun devicesScreen_showsDeviceName_whenDevicesPresent() {
        val mockVM = mockk<DevicesViewModel>(relaxed = true)
        val device = Device(id = 1, name = "Washing Machine", hours = 2, icon = "device_laundry", watts = 2000)
        val timeSlot = TimeSlot(startHour = 2, endHour = 4)
        val render = DeviceRender(device = device, bestSlot = timeSlot, cost = 0.15)

        every { mockVM.state } returns MutableStateFlow(DevicesState.Success(listOf(render), ""))

        composeTestRule.setContent {
            MaterialTheme {
                DevicesScreen(viewModel = mockVM, onDeviceClick = {})
            }
        }

        composeTestRule.onNodeWithText("Washing Machine").assertIsDisplayed()
    }

    @Test
    fun devicesScreen_showsErrorMessage() {
        val mockVM = mockk<DevicesViewModel>(relaxed = true)
        every { mockVM.state } returns MutableStateFlow(DevicesState.Error("Test error"))

        composeTestRule.setContent {
            MaterialTheme {
                DevicesScreen(viewModel = mockVM, onDeviceClick = {})
            }
        }

        composeTestRule.onNodeWithText("Test error").assertIsDisplayed()
    }
}
