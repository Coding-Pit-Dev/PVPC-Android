package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.DeviceRepository
import com.codingpit.pvpcplanner.domain.models.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteDeviceTest {
    private val mockRepository = mockk<DeviceRepository>()
    private lateinit var useCase: DeleteDevice

    @Before
    fun setup() {
        useCase = DeleteDevice(mockRepository)
    }

    @Test
    fun `invoke deletes device from repository`() =
        runTest {
            // Arrange
            val device = Device(1, "Washing Machine", 3, "washing_machine")
            coEvery { mockRepository.deleteDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.deleteDevice(device) }
        }

    @Test
    fun `invoke deletes device with all properties`() =
        runTest {
            // Arrange
            val device =
                Device(
                    id = 42,
                    name = "High Efficiency Dryer",
                    hours = 2,
                    icon = "dryer_premium",
                )
            coEvery { mockRepository.deleteDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.deleteDevice(device) }
        }

    @Test
    fun `invoke propagates repository exception`() =
        runTest {
            // Arrange
            val device = Device(1, "Non-existent Device", 1, "")
            val exception = IllegalArgumentException("Device not found")
            coEvery { mockRepository.deleteDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: IllegalArgumentException) {
                assertEquals(exception, e)
                coVerify { mockRepository.deleteDevice(device) }
            }
        }

    @Test
    fun `invoke handles database constraint exceptions`() =
        runTest {
            // Arrange
            val device = Device(1, "Referenced Device", 2, "icon")
            val exception = RuntimeException("FOREIGN KEY constraint failed")
            coEvery { mockRepository.deleteDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                coVerify { mockRepository.deleteDevice(device) }
            }
        }
}
