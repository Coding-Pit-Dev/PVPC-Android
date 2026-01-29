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

class AddDeviceTest {
    private val mockRepository = mockk<DeviceRepository>()
    private lateinit var useCase: AddDevice

    @Before
    fun setup() {
        useCase = AddDevice(mockRepository)
    }

    @Test
    fun `invoke adds device to repository`() =
        runTest {
            // Arrange
            val device = Device(1, "Washing Machine", 3, "washing_machine")
            coEvery { mockRepository.addDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.addDevice(device) }
        }

    @Test
    fun `invoke adds device with all properties`() =
        runTest {
            // Arrange
            val device =
                Device(
                    id = 42,
                    name = "High Efficiency Dryer",
                    hours = 2,
                    icon = "dryer_premium",
                )
            coEvery { mockRepository.addDevice(device) } returns Unit

            // Act
            useCase(device)

            // Assert
            coVerify { mockRepository.addDevice(device) }
        }

    @Test
    fun `invoke propagates repository exception`() =
        runTest {
            // Arrange
            val device = Device(1, "Invalid Device", -1, "")
            val exception = IllegalArgumentException("Invalid device data")
            coEvery { mockRepository.addDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: IllegalArgumentException) {
                assertEquals(exception, e)
                coVerify { mockRepository.addDevice(device) }
            }
        }

    @Test
    fun `invoke handles database constraint exceptions`() =
        runTest {
            // Arrange
            val device = Device(1, "Duplicate Device", 2, "icon")
            val exception = RuntimeException("UNIQUE constraint failed: device.id")
            coEvery { mockRepository.addDevice(device) } throws exception

            // Act & Assert
            try {
                useCase(device)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                coVerify { mockRepository.addDevice(device) }
            }
        }
}
