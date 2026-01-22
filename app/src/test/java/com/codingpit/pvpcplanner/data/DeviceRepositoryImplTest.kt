package com.codingpit.pvpcplanner.data

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.local.sources.DeviceLocalDataSource
import com.codingpit.pvpcplanner.domain.models.Device
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeviceRepositoryImplTest {

    private val mockLocalDataSource = mockk<DeviceLocalDataSource>()
    private lateinit var repository: DeviceRepositoryImpl

    @Before
    fun setup() {
        repository = DeviceRepositoryImpl(mockLocalDataSource)
    }

    @Test
    fun `getDevices returns flow from local data source`() = runTest {
        // Arrange
        val devices = listOf(
            Device(1, "Washing Machine", 3, "washing_machine"),
            Device(2, "Dishwasher", 2, "dishwasher")
        )
        val devicesFlow = flowOf(devices)
        every { mockLocalDataSource.getDevices() } returns devicesFlow

        // Act & Assert
        repository.getDevices().test {
            assertEquals(devices, awaitItem())
            awaitComplete()
        }
        verify { mockLocalDataSource.getDevices() }
    }

    @Test
    fun `addDevice calls local data source saveDevice`() = runTest {
        // Arrange
        val device = Device(1, "Washing Machine", 3, "washing_machine")
        coEvery { mockLocalDataSource.saveDevice(device) } returns Unit

        // Act
        repository.addDevice(device)

        // Assert
        coVerify { mockLocalDataSource.saveDevice(device) }
    }

    @Test
    fun `deleteDevice calls local data source deleteDevice`() = runTest {
        // Arrange
        val device = Device(1, "Washing Machine", 3, "washing_machine")
        coEvery { mockLocalDataSource.deleteDevice(device) } returns Unit

        // Act
        repository.deleteDevice(device)

        // Assert
        coVerify { mockLocalDataSource.deleteDevice(device) }
    }

    @Test
    fun `updateDevice calls local data source updateDevice`() = runTest {
        // Arrange
        val device = Device(1, "Updated Machine", 4, "updated_icon")
        coEvery { mockLocalDataSource.updateDevice(device) } returns Unit

        // Act
        repository.updateDevice(device)

        // Assert
        coVerify { mockLocalDataSource.updateDevice(device) }
    }

    @Test
    fun `addDevice propagates exception from local data source`() = runTest {
        // Arrange
        val device = Device(1, "Washing Machine", 3, "washing_machine")
        val exception = RuntimeException("Database constraint violation")
        coEvery { mockLocalDataSource.saveDevice(device) } throws exception

        // Act & Assert
        try {
            repository.addDevice(device)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.saveDevice(device) }
        }
    }

    @Test
    fun `deleteDevice propagates exception from local data source`() = runTest {
        // Arrange
        val device = Device(1, "Washing Machine", 3, "washing_machine")
        val exception = RuntimeException("Device not found")
        coEvery { mockLocalDataSource.deleteDevice(device) } throws exception

        // Act & Assert
        try {
            repository.deleteDevice(device)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.deleteDevice(device) }
        }
    }

    @Test
    fun `updateDevice propagates exception from local data source`() = runTest {
        // Arrange
        val device = Device(1, "Updated Machine", 4, "updated_icon")
        val exception = RuntimeException("Update failed")
        coEvery { mockLocalDataSource.updateDevice(device) } throws exception

        // Act & Assert
        try {
            repository.updateDevice(device)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.updateDevice(device) }
        }
    }
}