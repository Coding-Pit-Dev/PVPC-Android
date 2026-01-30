package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.CalculateDeviceCost
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class DevicesViewModelSimpleTest {
    private val mockGetDevices = mockk<GetDevices>()
    private val mockGetPricesFlow = mockk<GetPricesFlow>()
    private val mockAddDevice = mockk<AddDevice>(relaxed = true)
    private val mockDeleteDevice = mockk<DeleteDevice>(relaxed = true)
    private val mockCalculateBestTimeSlot = mockk<CalculateBestTimeSlot>()
    private val mockCalculateDeviceCost = mockk<CalculateDeviceCost>()
    private val mockErrorHandler = mockk<ErrorHandler>(relaxed = true)

    private lateinit var viewModel: DevicesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel can be created`() =
        runTest {
            every { mockGetDevices() } returns flowOf(emptyList())
            every { mockGetPricesFlow() } returns flowOf(Result.success(emptyList()))
            every { mockCalculateBestTimeSlot(any(), any()) } returns TimeSlot(0, 1)
            every { mockCalculateDeviceCost(any(), any(), any()) } returns 0.15

            viewModel =
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

            assertTrue(viewModel.state.value is DevicesState.Loading)
        }
}
