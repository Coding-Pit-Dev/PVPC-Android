package com.codingpit.pvpcplanner.integration

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.DeviceRepositoryImpl
import com.codingpit.pvpcplanner.data.local.sources.LocalDataSource
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.usecase.AddDevice
import com.codingpit.pvpcplanner.domain.usecase.DeleteDevice
import com.codingpit.pvpcplanner.domain.usecase.GetDevices
import com.codingpit.pvpcplanner.domain.usecase.UpdateDevice
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

class DeviceIntegrationTest {

    private val mockLocalDataSource = mockk<LocalDataSource>()

    private lateinit var repository: DeviceRepositoryImpl
    private lateinit var getDevicesUseCase: GetDevices
    private lateinit var addDeviceUseCase: AddDevice
    private lateinit var updateDeviceUseCase: UpdateDevice
    private lateinit var deleteDeviceUseCase: DeleteDevice

    @Before
    fun setup() {
        repository = DeviceRepositoryImpl(mockLocalDataSource)
        getDevicesUseCase = GetDevices(repository)
        addDeviceUseCase = AddDevice(repository)
        updateDeviceUseCase = UpdateDevice(repository)
        deleteDeviceUseCase = DeleteDevice(repository)
    }

    @Test
    fun `complete CRUD flow for devices`() = runTest {
        // Arrange
        val initialDevices = listOf(
            Device(1, "Washing Machine", 3, "washing_machine")
        )
        val newDevice = Device(2, "Dishwasher", 2, "dishwasher")
        val updatedDevice = Device(1, "Updated Washing Machine", 4, "updated_icon")

        every { mockLocalDataSource.getDevices() } returns flowOf(initialDevices) andThen flowOf(
            initialDevices + newDevice
        ) andThen flowOf(listOf(updatedDevice) + newDevice) andThen flowOf(listOf(updatedDevice))
        coEvery { mockLocalDataSource.saveDevice(newDevice) } returns Unit
        coEvery { mockLocalDataSource.updateDevice(updatedDevice) } returns Unit
        coEvery { mockLocalDataSource.deleteDevice(newDevice) } returns Unit

        // Act & Assert - Initial state
        getDevicesUseCase().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Washing Machine", list[0].name)
            awaitComplete()
        }

        // Act & Assert - Add device
        addDeviceUseCase(newDevice)
        coVerify { mockLocalDataSource.saveDevice(newDevice) }

        // Act & Assert - Update device
        updateDeviceUseCase(updatedDevice)
        coVerify { mockLocalDataSource.updateDevice(updatedDevice) }

        // Act & Assert - Delete device
        deleteDeviceUseCase(newDevice)
        coVerify { mockLocalDataSource.deleteDevice(newDevice) }

        verify { mockLocalDataSource.getDevices() }
    }

    @Test
    fun `device flow emits updates reactively`() = runTest {
        // Arrange
        val device1 = Device(1, "Device 1", 2, "icon1")
        val device2 = Device(2, "Device 2", 3, "icon2")

        every { mockLocalDataSource.getDevices() } returns flowOf(
            emptyList(),
            listOf(device1),
            listOf(device1, device2)
        )

        // Act & Assert
        getDevicesUseCase().test {
            val empty = awaitItem()
            assertEquals(0, empty.size)

            val oneDevice = awaitItem()
            assertEquals(1, oneDevice.size)
            assertEquals("Device 1", oneDevice[0].name)

            val twoDevices = awaitItem()
            assertEquals(2, twoDevices.size)
            assertEquals("Device 2", twoDevices[1].name)

            awaitComplete()
        }

        verify { mockLocalDataSource.getDevices() }
    }

    @Test
    fun `add device handles validation errors`() = runTest {
        // Arrange
        val invalidDevice = Device(-1, "", -1, "")
        val exception = IllegalArgumentException("Invalid device data")
        coEvery { mockLocalDataSource.saveDevice(invalidDevice) } throws exception

        // Act & Assert
        try {
            addDeviceUseCase(invalidDevice)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: IllegalArgumentException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.saveDevice(invalidDevice) }
        }
    }

    @Test
    fun `update device handles not found errors`() = runTest {
        // Arrange
        val nonExistentDevice = Device(999, "Non-existent", 2, "icon")
        val exception = RuntimeException("Device not found")
        coEvery { mockLocalDataSource.updateDevice(nonExistentDevice) } throws exception

        // Act & Assert
        try {
            updateDeviceUseCase(nonExistentDevice)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.updateDevice(nonExistentDevice) }
        }
    }

    @Test
    fun `delete device handles constraint violations`() = runTest {
        // Arrange
        val referencedDevice = Device(1, "Referenced Device", 2, "icon")
        val exception = RuntimeException("FOREIGN KEY constraint failed")
        coEvery { mockLocalDataSource.deleteDevice(referencedDevice) } throws exception

        // Act & Assert
        try {
            deleteDeviceUseCase(referencedDevice)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
            coVerify { mockLocalDataSource.deleteDevice(referencedDevice) }
        }
    }

    @Test
    fun `complete device lifecycle with realistic data`() = runTest {
        // Arrange - Simulate a typical device usage scenario
        val washingMachine = Device(1, "Samsung Washing Machine", 3, "washing_machine")
        val dishwasher = Device(2, "Bosch Dishwasher", 2, "dishwasher")
        val dryer = Device(3, "LG Dryer", 1, "dryer")

        every { mockLocalDataSource.getDevices() } returns flowOf(emptyList())
        coEvery { mockLocalDataSource.saveDevice(any()) } returns Unit
        coEvery { mockLocalDataSource.updateDevice(any()) } returns Unit

        // Act - Add multiple devices
        addDeviceUseCase(washingMachine)
        addDeviceUseCase(dishwasher)
        addDeviceUseCase(dryer)

        // Update washing machine duration
        val updatedWashingMachine = washingMachine.copy(hours = 2, name = "Samsung Quick Wash")
        updateDeviceUseCase(updatedWashingMachine)

        // Assert
        coVerify { mockLocalDataSource.saveDevice(washingMachine) }
        coVerify { mockLocalDataSource.saveDevice(dishwasher) }
        coVerify { mockLocalDataSource.saveDevice(dryer) }
        coVerify { mockLocalDataSource.updateDevice(updatedWashingMachine) }
    }

    @Test
    fun `device operations maintain data consistency`() = runTest {
        // Arrange
        val originalDevice = Device(1, "Original Name", 2, "original_icon")
        val duplicateIdDevice = Device(1, "Duplicate ID", 3, "duplicate_icon")
        val constraintException = RuntimeException("UNIQUE constraint failed: device.id")

        coEvery { mockLocalDataSource.saveDevice(originalDevice) } returns Unit
        coEvery { mockLocalDataSource.saveDevice(duplicateIdDevice) } throws constraintException

        // Act - Add original device successfully
        addDeviceUseCase(originalDevice)
        coVerify { mockLocalDataSource.saveDevice(originalDevice) }

        // Act - Attempt to add duplicate ID device
        try {
            addDeviceUseCase(duplicateIdDevice)
            assert(false) { "Expected constraint exception" }
        } catch (e: RuntimeException) {
            assertEquals(constraintException, e)
            coVerify { mockLocalDataSource.saveDevice(duplicateIdDevice) }
        }
    }

    @Test
    fun `batch device operations work correctly`() = runTest {
        // Arrange
        val devices = listOf(
            Device(1, "Device 1", 1, "icon1"),
            Device(2, "Device 2", 2, "icon2"),
            Device(3, "Device 3", 3, "icon3")
        )

        coEvery { mockLocalDataSource.saveDevice(any()) } returns Unit

        // Act - Add multiple devices in sequence
        devices.forEach { device ->
            addDeviceUseCase(device)
        }

        // Assert - All devices were saved
        devices.forEach { device ->
            coVerify { mockLocalDataSource.saveDevice(device) }
        }
    }
}
