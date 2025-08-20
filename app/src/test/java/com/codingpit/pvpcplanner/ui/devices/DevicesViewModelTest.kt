package com.codingpit.pvpcplanner.ui.devices

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import com.codingpit.pvpcplanner.domain.strategy.BestTimeSlotCalculationStrategy
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.CalculateBestTimeSlot
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.GetPricesFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DevicesViewModelTest {

    private val mockGetDevices = mockk<GetDevices>()
    private val mockGetPricesFlow = mockk<GetPricesFlow>()
    private val mockAddDevice = mockk<AddDevice>(relaxed = true)
    private val mockDeleteDevice = mockk<DeleteDevice>(relaxed = true)
    private val mockCalculateBestTimeSlot = mockk<CalculateBestTimeSlot>()
    
    private lateinit var viewModel: DevicesViewModel
    private val strategy = BestTimeSlotCalculationStrategy()

    @Before
    fun setup() {
        every { mockGetDevices() } returns flowOf(emptyList())
        every { mockGetPricesFlow() } returns flowOf(Result.success(emptyList()))
        
        // Mock the use case to call the real strategy for testing
        every { mockCalculateBestTimeSlot(any(), any()) } answers {
            strategy.calculateBestTimeSlot(firstArg(), secondArg())
        }
        
        viewModel = DevicesViewModel(
            mockGetDevices,
            mockGetPricesFlow,
            mockAddDevice,
            mockDeleteDevice,
            mockCalculateBestTimeSlot
        )
    }

    @Test
    fun `getBestSlot returns first slot when all prices are equal`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 2, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.15, 0.18),
            PVPCModel("2023-10-15", 2, 3, 0.15, 0.18),
            PVPCModel("2023-10-15", 3, 4, 0.15, 0.18)
        )

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(0, 2), result)
    }

    @Test
    fun `getBestSlot finds cheapest consecutive slot for 1-hour device`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 1, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.20, 0.18), // 0.20
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.18), // 0.10 <- cheapest
            PVPCModel("2023-10-15", 2, 3, 0.15, 0.18), // 0.15
            PVPCModel("2023-10-15", 3, 4, 0.25, 0.18)  // 0.25
        )

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(1, 2), result)
    }

    @Test
    fun `getBestSlot finds cheapest consecutive slot for 2-hour device`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 2, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.20, 0.18), // 0.20
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.18), // 0.10
            PVPCModel("2023-10-15", 2, 3, 0.05, 0.18), // 0.05
            PVPCModel("2023-10-15", 3, 4, 0.15, 0.18)  // 0.15
        )
        // Possible slots:
        // 0-2: 0.20 + 0.10 = 0.30
        // 1-3: 0.10 + 0.05 = 0.15 <- cheapest
        // 2-4: 0.05 + 0.15 = 0.20

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(1, 3), result)
    }

    @Test
    fun `getBestSlot finds cheapest consecutive slot for 3-hour device`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 3, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.30, 0.18), // 0.30
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.18), // 0.10
            PVPCModel("2023-10-15", 2, 3, 0.05, 0.18), // 0.05
            PVPCModel("2023-10-15", 3, 4, 0.20, 0.18), // 0.20
            PVPCModel("2023-10-15", 4, 5, 0.25, 0.18)  // 0.25
        )
        // Possible slots:
        // 0-3: 0.30 + 0.10 + 0.05 = 0.45
        // 1-4: 0.10 + 0.05 + 0.20 = 0.35 <- cheapest
        // 2-5: 0.05 + 0.20 + 0.25 = 0.50

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(1, 4), result)
    }

    @Test
    fun `getBestSlot handles minimum duration device with single price`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 1, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18)
        )

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(0, 1), result)
    }

    @Test
    fun `getBestSlot handles device duration equal to available slots`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 3, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.20, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.10, 0.18),
            PVPCModel("2023-10-15", 2, 3, 0.15, 0.18)
        )

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(0, 3), result) // Only one possible slot
    }

    @Test
    fun `getBestSlot prefers earlier slot when prices are tied`() = runTest {
        // Arrange
        val device = Device(1, "Test Device", 2, "test_icon")
        val prices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.10, 0.18), // 0.10
            PVPCModel("2023-10-15", 1, 2, 0.05, 0.18), // 0.05
            PVPCModel("2023-10-15", 2, 3, 0.05, 0.18), // 0.05
            PVPCModel("2023-10-15", 3, 4, 0.10, 0.18)  // 0.10
        )
        // Slots:
        // 0-2: 0.10 + 0.05 = 0.15 <- first occurrence of cheapest
        // 1-3: 0.05 + 0.05 = 0.10 <- cheaper
        // 2-4: 0.05 + 0.10 = 0.15

        // Act
        val result = strategy.calculateBestTimeSlot(device, prices)

        // Assert
        assertEquals(TimeSlot(1, 3), result)
    }
}