package com.codingpit.pvpcplanner.data.remote

import com.codingpit.pvpcplanner.domains.models.PVPCModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RemoteDataSourceTest{

    // SUT
    private lateinit var dataSource: RemoteDataSource

    // Dependencies
    private val api: PVPCApi = mockk()

    @BeforeEach
    fun setUp(){
        dataSource = RemoteDataSourceImpl(api)
    }

    @Test
    fun `getPrices should return a list of PVPCModel`() = runBlocking {

        val mockResponse = listOf(
            PVPCModel(1, "1", "22", "3", "4"),
            PVPCModel(1, "1", "23", "3", "4"),
            PVPCModel(1, "1", "24", "3", "4"),
        )

        coEvery { api.getPrices() } returns mockResponse

        val result = dataSource.getPrices()

        assertEquals(mockResponse, result)
        coVerify { api.getPrices() }

    }

    @Test
    fun `getPrices handles API exception`() = runBlocking {
        val exception = RuntimeException("API Error")

        coEvery { api.getPrices() } throws exception

        val result = runCatching { dataSource.getPrices() }

        assertEquals(exception, result.exceptionOrNull())
        coVerify { api.getPrices() }
    }
}