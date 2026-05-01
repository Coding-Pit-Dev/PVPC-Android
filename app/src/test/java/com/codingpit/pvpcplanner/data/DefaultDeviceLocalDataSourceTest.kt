package com.codingpit.pvpcplanner.data

import app.cash.turbine.test
import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.local.db.model.DeviceEntity
import com.codingpit.pvpcplanner.data.local.sources.DefaultDeviceLocalDataSource
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

class DefaultDeviceLocalDataSourceTest {
    private val mockDao = mockk<PVPCDao>()
    private lateinit var dataSource: DefaultDeviceLocalDataSource

    @Before
    fun setup() {
        dataSource = DefaultDeviceLocalDataSource(mockDao)
    }

    @Test
    fun `getDevices maps entities to domain models`() = runTest {
        val entities = listOf(
            DeviceEntity(1, "Washing Machine", 3, "washing_machine", 2000, "laundry"),
            DeviceEntity(2, "Dishwasher", 2, "dishwasher", 1500, "kitchen"),
        )
        every { mockDao.getDevices() } returns flowOf(entities)

        dataSource.getDevices().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals(1, result[0].id)
            assertEquals("Washing Machine", result[0].name)
            assertEquals(3, result[0].hours)
            assertEquals(2000, result[0].watts)
            assertEquals(2, result[1].id)
            awaitComplete()
        }
        verify { mockDao.getDevices() }
    }

    @Test
    fun `getDevices emits empty list when no devices`() = runTest {
        every { mockDao.getDevices() } returns flowOf(emptyList())

        dataSource.getDevices().test {
            assertEquals(emptyList<Device>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `saveDevice inserts entity to DAO`() = runTest {
        val device = Device(0, "Washing Machine", 3, "washing_machine", 2000, "laundry")
        coEvery { mockDao.insertDevice(any()) } returns Unit

        dataSource.saveDevice(device)

        coVerify { mockDao.insertDevice(match { it.name == "Washing Machine" && it.hours == 3 && it.watts == 2000 }) }
    }

    @Test
    fun `updateDevice updates entity in DAO`() = runTest {
        val device = Device(1, "Updated Machine", 4, "updated_icon", 2200, "laundry")
        coEvery { mockDao.updateDevice(any()) } returns Unit

        dataSource.updateDevice(device)

        coVerify { mockDao.updateDevice(match { it.id == 1 && it.name == "Updated Machine" && it.hours == 4 }) }
    }

    @Test
    fun `deleteDevice deletes entity from DAO`() = runTest {
        val device = Device(1, "Washing Machine", 3, "washing_machine", 2000, "laundry")
        coEvery { mockDao.deleteDevice(any()) } returns Unit

        dataSource.deleteDevice(device)

        coVerify { mockDao.deleteDevice(match { it.id == 1 && it.name == "Washing Machine" }) }
    }

    @Test
    fun `saveDevice propagates DAO exception`() = runTest {
        val device = Device(0, "Washing Machine", 3, "washing_machine", 2000, "laundry")
        val exception = RuntimeException("Database error")
        coEvery { mockDao.insertDevice(any()) } throws exception

        try {
            dataSource.saveDevice(device)
            org.junit.Assert.fail("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Database error", e.message)
        }
    }
}
