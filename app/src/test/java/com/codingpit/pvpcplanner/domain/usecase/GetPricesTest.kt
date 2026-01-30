package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPricesTest {
    private val mockRepository = mockk<PriceRepository>()
    private lateinit var useCase: GetPrices

    @Before
    fun setup() {
        useCase = GetPrices(mockRepository)
    }

    @Test
    fun `invoke with specific date returns prices from repository`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val expectedPrices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                    PVPCModel("2023-10-15", 1, 2, 0.14, 0.17),
                )
            coEvery { mockRepository.getPrices(date) } returns Result.success(expectedPrices)

            // Act
            val result = useCase(date)

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(expectedPrices, result.getOrNull())
            coVerify { mockRepository.getPrices(date) }
        }

    @Test
    fun `invoke with empty date uses current date`() =
        runTest {
            // Arrange
            val expectedPrices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                )
            // The use case will generate today's date when empty string is passed
            coEvery { mockRepository.getPrices(any()) } returns Result.success(expectedPrices)

            // Act
            val result = useCase("")

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(expectedPrices, result.getOrNull())
            coVerify { mockRepository.getPrices(any()) }
        }

    @Test
    fun `invoke with no parameters uses current date`() =
        runTest {
            // Arrange
            val expectedPrices =
                listOf(
                    PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
                )
            // The use case will generate today's date when no parameter is passed
            coEvery { mockRepository.getPrices(any()) } returns Result.success(expectedPrices)

            // Act
            val result = useCase()

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(expectedPrices, result.getOrNull())
            coVerify { mockRepository.getPrices(any()) }
        }

    @Test
    fun `invoke returns failure when repository fails`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val exception = RuntimeException("Network error")
            coEvery { mockRepository.getPrices(date) } returns Result.failure(exception)

            // Act
            val result = useCase(date)

            // Assert
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify { mockRepository.getPrices(date) }
        }

    @Test
    fun `invoke propagates repository exception`() =
        runTest {
            // Arrange
            val date = "2023-10-15"
            val exception = RuntimeException("Database connection failed")
            coEvery { mockRepository.getPrices(date) } throws exception

            // Act & Assert
            try {
                useCase(date)
                org.junit.Assert.fail("Expected exception to be thrown")
            } catch (e: RuntimeException) {
                assertEquals(exception, e)
                coVerify { mockRepository.getPrices(date) }
            }
        }
}
