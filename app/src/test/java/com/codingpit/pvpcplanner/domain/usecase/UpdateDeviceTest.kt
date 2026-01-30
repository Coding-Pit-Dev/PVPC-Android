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

class UpdateDeviceTest {
    private val mockRepository = mockk<DeviceRepository>()
    private lateinit var useCase: UpdateDevice

    @Before
    fun setup() {
        useCase = UpdateDevice(mockRepository)
    }

    @Test
    fun `invoke updates device in repository`() =
        runTest {
            // Arrange
            val device = Device(1, "Updated Washing Machine", 4, "washing_machine_new")
            coEvery { mockRepository.updateDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.updateDevice(device) }
        }

    @Test
    fun `invoke updates device with all properties`() =
        runTest {
            // Arrange
            val device =
                Device(
                    id = 42,
                    name = "Premium Smart Dryer",
                    hours = 3,
                    icon = "smart_dryer",
                )
            coEvery { mockRepository.updateDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.updateDevice(device) }
        }

    @Test
    fun `invoke updates device with minimal hours`() =
        runTest {
            // Arrange
            val device = Device(1, "Quick Wash", 1, "quick_wash")
            coEvery { mockRepository.updateDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.updateDevice(device) }
        }

    @Test
    fun `invoke propagates repository exception`() =
        runTest {
            // Arrange
            val device = Device(999, "Non-existent Device", 2, "icon")
            val exception = IllegalArgumentException("Device not found for update")
            coEvery { mockRepository.updateDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: IllegalArgumentException) {
                assertEquals(exception, e)
                coVerify { mockRepository.updateDevice(device) }
            }
        }

    @Test
    fun `invoke handles validation exceptions`() =
        runTest {
            // Arrange
            val device = Device(1, "", -1, "")
            val exception = IllegalArgumentException("Invalid device data")
            coEvery { mockRepository.updateDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: IllegalArgumentException) {
                assertEquals(exception, e)
                coVerify { mockRepository.updateDevice(device) }
            }
        }

    @Test
    fun `invoke handles database constraint exceptions`() =
        runTest {
            // Arrange
            val device = Device(1, "Duplicate Name", 2, "icon")
            val exception = RuntimeException("UNIQUE constraint failed: device.name")
            coEvery { mockRepository.updateDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                coVerify { mockRepository.updateDevice(device) }
            }
        }
}
