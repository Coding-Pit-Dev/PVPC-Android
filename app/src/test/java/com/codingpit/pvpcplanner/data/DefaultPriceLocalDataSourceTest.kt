package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.local.db.model.PVPCEntity
import com.codingpit.pvpcplanner.data.local.sources.DefaultPriceLocalDataSource
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultPriceLocalDataSourceTest {
    private val mockDao = mockk<PVPCDao>()
    private lateinit var dataSource: DefaultPriceLocalDataSource

    @Before
    fun setup() {
        dataSource = DefaultPriceLocalDataSource(mockDao)
    }

    @Test
    fun `getPrices converts ISO date to dd-MM-yyyy and maps entities`() =
        runTest {
            val entities =
                listOf(
                    PVPCEntity("01/05/2026-0", "01/05/2026", 0, 1, 0.15, 0.01),
                    PVPCEntity("01/05/2026-1", "01/05/2026", 1, 2, 0.12, 0.01),
                )
            every { mockDao.getPrices("01/05/2026") } returns entities

            val result = dataSource.getPrices("2026-05-01")

            assertEquals(2, result.size)
            assertEquals(0, result[0].startHour)
            assertEquals(1, result[0].endHour)
            assertEquals(0.15, result[0].pcb, 0.001)
            verify { mockDao.getPrices("01/05/2026") }
        }

    @Test
    fun `getPrices returns empty list for invalid date format`() =
        runTest {
            val result = dataSource.getPrices("not-a-date")
            assertTrue(result.isEmpty())
        }

    @Test
    fun `getPrices returns empty list when DAO returns nothing`() =
        runTest {
            every { mockDao.getPrices(any()) } returns emptyList()

            val result = dataSource.getPrices("2026-05-01")
            assertTrue(result.isEmpty())
        }

    @Test
    fun `savePrices inserts mapped entities into DAO`() =
        runTest {
            val prices =
                listOf(
                    PVPCModel("01/05/2026", 0, 1, 0.15, 0.01),
                    PVPCModel("01/05/2026", 1, 2, 0.12, 0.01),
                )
            every { mockDao.insertAll(any()) } returns Unit

            dataSource.savePrices(prices)

            coVerify {
                mockDao.insertAll(
                    match { entities ->
                        entities.size == 2 &&
                            entities[0].day == "01/05/2026" &&
                            entities[0].startHour == 0 &&
                            entities[0].endHour == 1 &&
                            entities[0].pcb == 0.15 &&
                            entities[1].startHour == 1 &&
                            entities[1].endHour == 2 &&
                            entities[1].pcb == 0.12
                    },
                )
            }
        }

    @Test
    fun `getAvailableDays delegates to DAO`() {
        val days = listOf("01/05/2026", "30/04/2026", "29/04/2026")
        every { mockDao.getAvailableDays() } returns days

        val result = dataSource.getAvailableDays()

        assertEquals(days, result)
        verify { mockDao.getAvailableDays() }
    }

    @Test
    fun `getPricesByStoredDay delegates to DAO with raw day string`() {
        val entities =
            listOf(
                PVPCEntity("01/05/2026-0", "01/05/2026", 0, 1, 0.15, 0.01),
            )
        every { mockDao.getPrices("01/05/2026") } returns entities

        val result = dataSource.getPricesByStoredDay("01/05/2026")

        assertEquals(1, result.size)
        verify { mockDao.getPrices("01/05/2026") }
    }
}
