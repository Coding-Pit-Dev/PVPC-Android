package com.codingpit.pvpcplanner.integration

import com.codingpit.pvpcplanner.data.PriceRepositoryImpl
import com.codingpit.pvpcplanner.data.local.sources.LocalDataSource
import com.codingpit.pvpcplanner.data.remote.RemoteDataSource
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PriceIntegrationTest {

    private val mockRemoteDataSource = mockk<RemoteDataSource>()
    private val mockLocalDataSource = mockk<LocalDataSource>()

    private lateinit var repository: PriceRepositoryImpl
    private lateinit var getPricesUseCase: GetPrices

    @Before
    fun setup() {
        repository = PriceRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)
        getPricesUseCase = GetPrices(repository)
    }

    @Test
    fun `complete flow - local cache hit returns data without remote call`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val cachedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.14, 0.17)
        )
        coEvery { mockLocalDataSource.getPrices(date) } returns cachedPrices

        // Act
        val result = getPricesUseCase(date)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(cachedPrices, result.getOrNull())
        coVerify { mockLocalDataSource.getPrices(date) }
        coVerify(exactly = 0) { mockRemoteDataSource.getPrices(any()) }
    }

    @Test
    fun `complete flow - cache miss triggers remote fetch and local save`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val remotePrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.14, 0.17)
        )
        coEvery { mockLocalDataSource.getPrices(date) } returns emptyList()
        coEvery { mockRemoteDataSource.getPrices(date) } returns remotePrices
        coEvery { mockLocalDataSource.savePrices(remotePrices) } returns Unit

        // Act
        val result = getPricesUseCase(date)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(remotePrices, result.getOrNull())
        coVerify { mockLocalDataSource.getPrices(date) }
        coVerify { mockRemoteDataSource.getPrices(date) }
        coVerify { mockLocalDataSource.savePrices(remotePrices) }
    }

    @Test
    fun `complete flow - remote failure returns error result`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val exception = RuntimeException("Network error")
        coEvery { mockLocalDataSource.getPrices(date) } returns emptyList()
        coEvery { mockRemoteDataSource.getPrices(date) } throws exception

        // Act
        val result = getPricesUseCase(date)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { mockLocalDataSource.getPrices(date) }
        coVerify { mockRemoteDataSource.getPrices(date) }
        coVerify(exactly = 0) { mockLocalDataSource.savePrices(any()) }
    }

    @Test
    fun `complete flow - local failure returns error result`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val exception = RuntimeException("Database error")
        coEvery { mockLocalDataSource.getPrices(date) } throws exception

        // Act
        val result = getPricesUseCase(date)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { mockLocalDataSource.getPrices(date) }
        coVerify(exactly = 0) { mockRemoteDataSource.getPrices(any()) }
    }

    @Test
    fun `complete flow - use case with default date calls repository correctly`() = runTest {
        // Arrange
        val expectedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18)
        )
        coEvery { mockLocalDataSource.getPrices(any()) } returns expectedPrices

        // Act
        val result = getPricesUseCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedPrices, result.getOrNull())
        coVerify { mockLocalDataSource.getPrices(any()) }
    }

    @Test
    fun `complete flow - multiple sequential calls use cache correctly`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val cachedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18)
        )
        coEvery { mockLocalDataSource.getPrices(date) } returns cachedPrices

        // Act
        val result1 = getPricesUseCase(date)
        val result2 = getPricesUseCase(date)

        // Assert
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(cachedPrices, result1.getOrNull())
        assertEquals(cachedPrices, result2.getOrNull())
        coVerify(exactly = 2) { mockLocalDataSource.getPrices(date) }
        coVerify(exactly = 0) { mockRemoteDataSource.getPrices(any()) }
    }
}
