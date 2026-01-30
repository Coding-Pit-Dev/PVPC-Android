package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.UpdateDevice
import com.codingpit.pvpcplanner.utils.CategoryUiModel
import com.codingpit.pvpcplanner.utils.DeviceIcon
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceAddViewModelTest {
    private val mockAddDevice = mockk<AddDevice>(relaxed = true)
    private val mockUpdateDevice = mockk<UpdateDevice>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DeviceAddViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DeviceAddViewModel(mockAddDevice, mockUpdateDevice, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveDevice rounds watts correctly`() =
        runTest {
            val icons = listOf(DeviceIcon("icon1", Icons.Default.Add, 0))
            val categories = listOf(CategoryUiModel("cat1", 0))
            viewModel.initialize(icons, categories)

            viewModel.onDeviceNameChanged("Test Device")
            viewModel.onHoursChanged("2")
            viewModel.onWattsChanged("10.6") // Should round to 11

            viewModel.saveDevice {}
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify {
                mockAddDevice(
                    withArg { device ->
                        assertEquals(11, device.watts)
                    },
                )
            }
        }

    @Test
    fun `saveDevice persists notes`() =
        runTest {
            val icons = listOf(DeviceIcon("icon1", Icons.Default.Add, 0))
            val categories = listOf(CategoryUiModel("cat1", 0))
            viewModel.initialize(icons, categories)

            viewModel.onDeviceNameChanged("Test Device")
            viewModel.onHoursChanged("2")
            viewModel.onWattsChanged("100")
            viewModel.onNotesChanged("My important note")

            viewModel.saveDevice {}
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify {
                mockAddDevice(
                    withArg { device ->
                        assertEquals("My important note", device.notes)
                    },
                )
            }
        }

    @Test
    fun `alwaysOn clears hours error`() =
        runTest {
            val icons = listOf(DeviceIcon("icon1", Icons.Default.Add, 0))
            val categories = listOf(CategoryUiModel("cat1", 0))
            viewModel.initialize(icons, categories)

            // Set invalid hours
            viewModel.onHoursChanged("25")

            val stateWithErr = viewModel.state.value as DeviceAddState.Success
            assertEquals("error_validation_device_hours_max", stateWithErr.hoursError)

            // Enable Always On
            viewModel.onAlwaysOnChanged(true)

            val stateAlwaysOn = viewModel.state.value as DeviceAddState.Success
            assertEquals(null, stateAlwaysOn.hoursError)
            assertEquals("24", stateAlwaysOn.hours)
        }
}
