package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.remote.PVPCApi
import com.codingpit.pvpcplanner.data.remote.RemoteDataSourceImpl
import com.codingpit.pvpcplanner.data.remote.response.PVPCDTO
import com.codingpit.pvpcplanner.data.remote.response.PVPCResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RemoteDataSourceImplTest {
    private val mockApi = mockk<PVPCApi>()
    private lateinit var dataSource: RemoteDataSourceImpl

    @Before
    fun setup() {
        dataSource = RemoteDataSourceImpl(mockApi)
    }

    private fun buildDto(day: String, hour: String, pcb: String = "100,00000", cym: String = "10,00000") = PVPCDTO(
        day = day, hour = hour, pcb = pcb, cym = cym,
        cof2td = null, pmhpcb = null, pmhcyM = null, sahpcb = null, sahcym = null,
        fompcb = null, fomcym = null, fospcb = null, foscyM = null, intpcb = null,
        intcym = null, cappcb = null, capcym = null, teupcb = null, teucym = null,
        ccvpcb = null, ccvcyM = null, edsrpcb = null, edsrcym = null, tahpcb = null, tahcym = null,
    )

    @Test
    fun `getPrices maps DTO list to domain models`() = runTest {
        val dtos = listOf(
            buildDto("01/05/2026", "0-1", "100,00000", "10,00000"),
            buildDto("01/05/2026", "1-2", "120,00000", "12,00000"),
        )
        coEvery { mockApi.getPrices("2026-05-01") } returns PVPCResponse(dtos)

        val result = dataSource.getPrices("2026-05-01")

        assertEquals(2, result.size)
        assertEquals(0, result[0].startHour)
        assertEquals(1, result[0].endHour)
        assertEquals(0.1, result[0].pcb, 0.001)
        assertEquals(1, result[1].startHour)
        coVerify { mockApi.getPrices("2026-05-01") }
    }

    @Test
    fun `getPrices returns empty list when API returns empty pvpc list`() = runTest {
        coEvery { mockApi.getPrices(any()) } returns PVPCResponse(emptyList())

        val result = dataSource.getPrices("2026-05-01")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getPrices propagates IOException from API`() = runTest {
        coEvery { mockApi.getPrices(any()) } throws IOException("Network unreachable")

        try {
            dataSource.getPrices("2026-05-01")
            org.junit.Assert.fail("Expected IOException")
        } catch (e: IOException) {
            assertEquals("Network unreachable", e.message)
        }
    }

    @Test
    fun `getPrices propagates RuntimeException from API`() = runTest {
        coEvery { mockApi.getPrices(any()) } throws RuntimeException("HTTP 404")

        try {
            dataSource.getPrices("2026-05-01")
            org.junit.Assert.fail("Expected RuntimeException")
        } catch (e: RuntimeException) {
            assertEquals("HTTP 404", e.message)
        }
    }

    @Test
    fun `getPrices passes date parameter to API unchanged`() = runTest {
        val date = "2026-04-30"
        coEvery { mockApi.getPrices(date) } returns PVPCResponse(emptyList())

        dataSource.getPrices(date)

        coVerify { mockApi.getPrices(date) }
    }
}
