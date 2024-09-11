package com.codingpit.pvpcplanner.data.remote

import android.util.Log
import com.codingpit.pvpcplanner.domains.models.PVPCModel
import com.codingpit.pvpcplanner.mocks.Mocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoteDataSourceTest {

    // SUT
    private lateinit var dataSource: RemoteDataSource
    private lateinit var mocks: Mocks

    // Dependencies
    private val api: PVPCApi = mockk()

    @BeforeEach
    fun setUp() {
        mocks = Mocks()
        dataSource = RemoteDataSourceImpl(api)
    }

    @Test
    fun `Given successful response When getPrices Then return a list of PVPCModel`() = runBlocking {
        // Given
        coEvery { api.getPrices() } returns mocks.mockPVPCModel

        // When
        val result = dataSource.getPrices()

        // Then
        assertEquals(mocks.mockPVPCModel, result)
        coVerify { api.getPrices() }
    }

    @Test
    fun `Given API throws an exception When getPrices is called Then exception is returned`() = runBlocking {
        // Given
        val exception = RuntimeException("API Error")
        coEvery { api.getPrices() } throws exception

        // When
        val result = runCatching { dataSource.getPrices() }

        // Then
        assertEquals(exception, result.exceptionOrNull())
        coVerify { api.getPrices() }
    }
}
