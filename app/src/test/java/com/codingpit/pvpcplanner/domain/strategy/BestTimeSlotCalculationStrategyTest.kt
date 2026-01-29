package com.codingpit.pvpcplanner.domain.strategy

import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeSlot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BestTimeSlotCalculationStrategyTest {
    private lateinit var strategy: BestTimeSlotCalculationStrategy

    @Before
    fun setup() {
        strategy = BestTimeSlotCalculationStrategy()
    }

    @Test
    fun `calculateBestTimeSlot returns first slot when all prices are equal`() =
        runTest {
            // Arrange
            val device = Device(1, "Test Device", 2, "test_icon")
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                    PVPCModel("2023-10-15", 1, 2, 0.15, 0.18),
                    PVPCModel("2023-10-15", 2, 3, 0.15, 0.18),
                    PVPCModel("2023-10-15", 3, 4, 0.15, 0.18),
                )

            // Act
            val result = strategy.calculateBestTimeSlot(device, prices)

            // Assert
            assertEquals(TimeSlot(0, 2), result)
        }

    @Test
    fun `calculateBestTimeSlot finds cheapest consecutive slot for 1-hour device`() =
        runTest {
            // Arrange
            val device = Device(1, "Test Device", 1, "test_icon")
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.20, 0.18), // 0.20
                    PVPCModel("2023-10-15", 1, 2, 0.10, 0.18), // 0.10 <- cheapest
                    PVPCModel("2023-10-15", 2, 3, 0.15, 0.18), // 0.15
                    PVPCModel("2023-10-15", 3, 4, 0.25, 0.18), // 0.25
                )

            // Act
            val result = strategy.calculateBestTimeSlot(device, prices)

            // Assert
            assertEquals(TimeSlot(1, 2), result)
        }

    @Test
    fun `calculateBestTimeSlot finds cheapest consecutive slot for multi-hour device`() =
        runTest {
            // Arrange
            val device = Device(1, "Test Device", 3, "test_icon")
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.30, 0.18), // 0.30
                    PVPCModel("2023-10-15", 1, 2, 0.10, 0.18), // 0.10
                    PVPCModel("2023-10-15", 2, 3, 0.05, 0.18), // 0.05
                    PVPCModel("2023-10-15", 3, 4, 0.20, 0.18), // 0.20
                    PVPCModel("2023-10-15", 4, 5, 0.25, 0.18), // 0.25
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
    fun `calculateBestTimeSlot handles minimum duration device with single price`() =
        runTest {
            // Arrange
            val device = Device(1, "Test Device", 1, "test_icon")
            val prices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                )

            // Act
            val result = strategy.calculateBestTimeSlot(device, prices)

            // Assert
            assertEquals(TimeSlot(0, 1), result)
        }
}
