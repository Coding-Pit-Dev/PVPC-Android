package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.utils.DateChecker
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetDefaultDateTest {
    private val mockDateChecker = mockk<DateChecker>()
    private lateinit var getDefaultDate: GetDefaultDate

    @Before
    fun setup() {
        getDefaultDate = GetDefaultDate(mockDateChecker)
    }

    @Test
    fun `invoke delegates to dateChecker getDefaultDate`() {
        // Arrange
        val expectedDate = LocalDate.of(2024, 5, 20)
        every { mockDateChecker.getDefaultDate() } returns expectedDate

        // Act
        val result = getDefaultDate()

        // Assert
        assertEquals(expectedDate, result)
        verify(exactly = 1) { mockDateChecker.getDefaultDate() }
    }

    @Test
    fun `invoke returns today when dateChecker returns today`() {
        // Arrange
        val today = LocalDate.now()
        every { mockDateChecker.getDefaultDate() } returns today

        // Act
        val result = getDefaultDate()

        // Assert
        assertEquals(today, result)
    }

    @Test
    fun `invoke returns tomorrow when dateChecker returns tomorrow`() {
        // Arrange: after 21:00 data for next day is available
        val tomorrow = LocalDate.now().plusDays(1)
        every { mockDateChecker.getDefaultDate() } returns tomorrow

        // Act
        val result = getDefaultDate()

        // Assert
        assertEquals(tomorrow, result)
    }
}
