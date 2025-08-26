package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.utils.DateChecker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetPricesFlowTest {

    private val mockRepository = mockk<PriceRepository>()
    private val mockDateChecker = mockk<DateChecker>()
    private lateinit var useCase: GetPricesFlow

    @Before
    fun setup() {
        useCase = GetPricesFlow(mockRepository, mockDateChecker)
    }

    @Test
    fun `invoke with specific date returns prices flow from repository`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val expectedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18),
            PVPCModel("2023-10-15", 1, 2, 0.14, 0.17)
        )
        coEvery { mockRepository.getPrices(date) } returns Result.success(expectedPrices)

        // Act
        val result = useCase(date)

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isSuccess)
        assertEquals(expectedPrices, resultList.first().getOrNull())
        coVerify { mockRepository.getPrices(date) }
        verify(exactly = 0) { mockDateChecker.getDefaultDate() }
    }

    @Test
    fun `invoke with empty date uses default date from date checker`() = runTest {
        // Arrange
        val defaultDate = LocalDate.of(2023, 10, 15)
        val expectedDateString = "2023-10-15"
        val expectedPrices = listOf(
            PVPCModel("2023-10-15", 0, 1, 0.15, 0.18)
        )
        every { mockDateChecker.getDefaultDate() } returns defaultDate
        coEvery { mockRepository.getPrices(expectedDateString) } returns Result.success(expectedPrices)

        // Act
        val result = useCase("")

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isSuccess)
        assertEquals(expectedPrices, resultList.first().getOrNull())
        verify { mockDateChecker.getDefaultDate() }
        coVerify { mockRepository.getPrices(expectedDateString) }
    }

    @Test
    fun `invoke with no parameters uses default date from date checker`() = runTest {
        // Arrange
        val defaultDate = LocalDate.of(2023, 10, 16)
        val expectedDateString = "2023-10-16"
        val expectedPrices = listOf(
            PVPCModel("2023-10-16", 0, 1, 0.16, 0.19)
        )
        every { mockDateChecker.getDefaultDate() } returns defaultDate
        coEvery { mockRepository.getPrices(expectedDateString) } returns Result.success(expectedPrices)

        // Act
        val result = useCase()

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isSuccess)
        assertEquals(expectedPrices, resultList.first().getOrNull())
        verify { mockDateChecker.getDefaultDate() }
        coVerify { mockRepository.getPrices(expectedDateString) }
    }

    @Test
    fun `invoke returns failure flow when repository fails`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val exception = RuntimeException("Network error")
        coEvery { mockRepository.getPrices(date) } returns Result.failure(exception)

        // Act
        val result = useCase(date)

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isFailure)
        assertEquals(exception, resultList.first().exceptionOrNull())
        coVerify { mockRepository.getPrices(date) }
    }

    @Test
    fun `invoke handles repository exception with default date`() = runTest {
        // Arrange
        val defaultDate = LocalDate.of(2023, 10, 15)
        val expectedDateString = "2023-10-15"
        val exception = RuntimeException("Database connection failed")
        every { mockDateChecker.getDefaultDate() } returns defaultDate
        coEvery { mockRepository.getPrices(expectedDateString) } returns Result.failure(exception)

        // Act
        val result = useCase("")

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isFailure)
        assertEquals(exception, resultList.first().exceptionOrNull())
        verify { mockDateChecker.getDefaultDate() }
        coVerify { mockRepository.getPrices(expectedDateString) }
    }

    @Test
    fun `invoke handles valid future date with specific date`() = runTest {
        // Arrange
        val futureDate = "2023-12-25"
        val expectedPrices = listOf(
            PVPCModel("2023-12-25", 0, 1, 0.12, 0.15)
        )
        coEvery { mockRepository.getPrices(futureDate) } returns Result.success(expectedPrices)

        // Act
        val result = useCase(futureDate)

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isSuccess)
        assertEquals(expectedPrices, resultList.first().getOrNull())
        coVerify { mockRepository.getPrices(futureDate) }
    }

    @Test
    fun `invoke handles empty prices list`() = runTest {
        // Arrange
        val date = "2023-10-15"
        val emptyPrices = emptyList<PVPCModel>()
        coEvery { mockRepository.getPrices(date) } returns Result.success(emptyPrices)

        // Act
        val result = useCase(date)

        // Assert
        val resultList = result.toList()
        assertEquals(1, resultList.size)
        assertTrue(resultList.first().isSuccess)
        assertEquals(emptyPrices, resultList.first().getOrNull())
        coVerify { mockRepository.getPrices(date) }
    }
}