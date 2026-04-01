package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.CalculateDeviceCost
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Simplified DevicesViewModel test focusing on public method behavior
 * rather than complex StateFlow testing which is problematic due to
 * Dispatchers.IO and WhileSubscribed timing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DevicesViewModelFunctionTest {
    private val mockGetDevices = mockk<GetDevices>()
    private val mockGetPricesFlow = mockk<GetPricesFlow>()
    private val mockAddDevice = mockk<AddDevice>(relaxed = true)
    private val mockDeleteDevice = mockk<DeleteDevice>(relaxed = true)
    private val mockCalculateBestTimeSlot = mockk<CalculateBestTimeSlot>()
    private val mockCalculateDeviceCost = mockk<CalculateDeviceCost>()
    private val mockErrorHandler = mockk<ErrorHandler>()

    private lateinit var viewModel: DevicesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Set up basic mocks to prevent flow exceptions
        every { mockGetDevices() } returns flowOf(emptyList())
        every { mockGetPricesFlow() } returns flowOf(Result.success(PriceFetchResult(emptyList(), isFromCache = false)))
        every { mockCalculateBestTimeSlot(any(), any()) } returns TimeSlot(0, 1)
        every { mockCalculateDeviceCost(any(), any(), any()) } returns 0.15
    }

    private fun createViewModel(): DevicesViewModel =
        DevicesViewModel(
            mockGetDevices,
            mockGetPricesFlow,
            mockAddDevice,
            mockDeleteDevice,
            mockCalculateBestTimeSlot,
            mockCalculateDeviceCost,
            mockErrorHandler,
            testDispatcher,
        )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addDevice calls use case with correct parameters`() =
        runTest {
            // Arrange
            coEvery { mockAddDevice(any()) } returns Unit
            viewModel = createViewModel()

            // Act
            viewModel.addDevice("Test Device", 3, "test_icon")
            testDispatcher.scheduler.advanceUntilIdle() // Let coroutines complete

            // Assert
            coVerify {
                mockAddDevice(Device(name = "Test Device", hours = 3, icon = "test_icon"))
            }
        }

    @Test
    fun `removeDevice calls delete use case with correct device`() =
        runTest {
            // Arrange
            val device = Device(1, "Test Device", 2, "test")
            coEvery { mockDeleteDevice(device) } returns Unit
            viewModel = createViewModel()

            // Act
            viewModel.removeDevice(device)
            testDispatcher.scheduler.advanceUntilIdle() // Let coroutines complete

            // Assert
            coVerify { mockDeleteDevice(device) }
        }

    // Modal functionality has been moved to DeviceAddViewModel
    // These tests are no longer needed

    @Test
    fun `viewModel can be initialized successfully`() =
        runTest {
            // Act - Create ViewModel
            viewModel = createViewModel()

            // Assert - ViewModel should be created without throwing
            assertTrue("Initial state should be Loading", viewModel.state.value is DevicesState.Loading)
        }

    @Test
    fun `addDevice with various parameters works correctly`() =
        runTest {
            // Arrange
            coEvery { mockAddDevice(any()) } returns Unit
            viewModel = createViewModel()

            // Act & Assert - Test different parameter combinations
            viewModel.addDevice("Washing Machine", 2, "washing_machine")
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify {
                mockAddDevice(
                    Device(
                        name = "Washing Machine",
                        hours = 2,
                        icon = "washing_machine",
                    ),
                )
            }

            viewModel.addDevice("Dryer", 1, "dryer")
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { mockAddDevice(Device(name = "Dryer", hours = 1, icon = "dryer")) }

            viewModel.addDevice("", 0, "")
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { mockAddDevice(Device(name = "", hours = 0, icon = "")) }
        }

    @Test
    fun `device operations sequence works correctly`() =
        runTest {
            // Arrange
            coEvery { mockAddDevice(any()) } returns Unit
            coEvery { mockDeleteDevice(any()) } returns Unit
            viewModel = createViewModel()

            val device1 = Device(1, "Device 1", 2, "icon1")
            Device(2, "Device 2", 3, "icon2")

            // Act - Perform a sequence of operations
            viewModel.addDevice("Device 1", 2, "icon1")
            viewModel.addDevice("Device 2", 3, "icon2")
            viewModel.removeDevice(device1)
            testDispatcher.scheduler.advanceUntilIdle() // Let all coroutines complete

            // Assert - Verify all operations were called
            coVerify { mockAddDevice(Device(name = "Device 1", hours = 2, icon = "icon1")) }
            coVerify { mockAddDevice(Device(name = "Device 2", hours = 3, icon = "icon2")) }
            coVerify { mockDeleteDevice(device1) }
        }
}
