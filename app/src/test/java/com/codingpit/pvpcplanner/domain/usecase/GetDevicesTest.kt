package com.codingpit.pvpcplanner.domain.usecase

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.DeviceRepository
import com.codingpit.pvpcplanner.domain.models.Device
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetDevicesTest {
    private val mockRepository = mockk<DeviceRepository>()
    private lateinit var useCase: GetDevices

    @Before
    fun setup() {
        useCase = GetDevices(mockRepository)
    }

    @Test
    fun `invoke returns device flow from repository`() =
        runTest {
            // Arrange
            val devices =
                listOf(
                    Device(1, "Washing Machine", 3, "washing_machine"),
                    Device(2, "Dishwasher", 2, "dishwasher"),
                    Device(3, "Dryer", 1, "dryer"),
                )
            val deviceFlow = flowOf(devices)
            every { mockRepository.getDevices() } returns deviceFlow

            // Act
            val result = useCase()

            // Assert
            assertEquals(deviceFlow, result)
            verify { mockRepository.getDevices() }
        }

    @Test
    fun `invoke returns empty flow when repository returns empty`() =
        runTest {
            // Arrange
            val emptyFlow = flowOf<List<Device>>(emptyList())
            every { mockRepository.getDevices() } returns emptyFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                assertEquals(emptyList<Device>(), awaitItem())
                awaitComplete()
            }
            verify { mockRepository.getDevices() }
        }

    @Test
    fun `invoke handles multiple emissions from repository`() =
        runTest {
            // Arrange
            val firstDevices = listOf(Device(1, "Device 1", 2, "icon1"))
            val secondDevices =
                listOf(
                    Device(1, "Device 1", 2, "icon1"),
                    Device(2, "Device 2", 3, "icon2"),
                )
            val multiEmissionFlow = flowOf(firstDevices, secondDevices)
            every { mockRepository.getDevices() } returns multiEmissionFlow

            // Act
            val result = useCase()

            // Assert
            result.test {
                assertEquals(firstDevices, awaitItem())
                assertEquals(secondDevices, awaitItem())
                awaitComplete()
            }
            verify { mockRepository.getDevices() }
        }
}
