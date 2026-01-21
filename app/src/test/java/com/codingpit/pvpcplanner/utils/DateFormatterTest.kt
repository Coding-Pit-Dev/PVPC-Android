package com.codingpit.pvpcplanner.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateFormatterTest {

    @Test
    fun `formatDate with LocalDate returns correct format`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 15)

        // Act
        val result = DateFormatter.formatDate(date)

        // Assert
        assertEquals("2023-10-15", result)
    }

    @Test
    fun `formatDate with components returns correct format`() {
        // Arrange & Act
        val result = DateFormatter.formatDate(2023, 10, 15)

        // Assert
        assertEquals("2023-10-15", result)
    }

    @Test
    fun `formatDate handles single digit month and day correctly`() {
        // Arrange & Act
        val result = DateFormatter.formatDate(2023, 3, 5)

        // Assert
        assertEquals("2023-03-05", result)
    }

    @Test
    fun `isSameDay returns true for identical dates`() {
        // Arrange
        val date1 = LocalDate.of(2023, 10, 15)
        val date2 = LocalDate.of(2023, 10, 15)

        // Act & Assert
        assertTrue(DateFormatter.isSameDay(date1, date2))
    }

    @Test
    fun `isSameDay returns false for different dates`() {
        // Arrange
        val date1 = LocalDate.of(2023, 10, 15)
        val date2 = LocalDate.of(2023, 10, 16)

        // Act & Assert
        assertFalse(DateFormatter.isSameDay(date1, date2))
    }

    @Test
    fun `isSameDay returns false for different months`() {
        // Arrange
        val date1 = LocalDate.of(2023, 10, 15)
        val date2 = LocalDate.of(2023, 11, 15)

        // Act & Assert
        assertFalse(DateFormatter.isSameDay(date1, date2))
    }

    @Test
    fun `isSameDay returns false for different years`() {
        // Arrange
        val date1 = LocalDate.of(2023, 10, 15)
        val date2 = LocalDate.of(2024, 10, 15)

        // Act & Assert
        assertFalse(DateFormatter.isSameDay(date1, date2))
    }

    @Test
    fun `toParsedDate extension function works correctly`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 15)

        // Act
        val result = date.toParsedDate()

        // Assert
        assertEquals("2023-10-15", result)
    }

    @Test
    fun `constants have expected values`() {
        // Assert
        assertEquals("%04d-%02d-%02d", DateFormatter.DEFAULT_DATE_FORMAT)
        assertEquals("Europe/Madrid", DateFormatter.DEFAULT_TIME_ZONE)
        assertEquals(21, DateFormatter.DEFAULT_REFRESH_DATA_HOUR)
    }
}