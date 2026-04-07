package com.codingpit.pvpcplanner.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DateCheckerTest {
    private lateinit var dateChecker: DateChecker

    @Before
    fun setup() {
        dateChecker = DateChecker()
    }

    @Test
    fun `getDefaultDate returns a non-null LocalDate`() {
        // Act
        val result = dateChecker.getDefaultDate()

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `getDefaultDate result matches DateFormatter getDefaultQueryDate`() {
        // Act
        val checkerResult = dateChecker.getDefaultDate()
        val formatterResult = DateFormatter.getDefaultQueryDate()

        // Assert: Both delegate to the same logic so results should be equal
        assertEquals(formatterResult, checkerResult)
    }

    @Test
    fun `checkValidDate returns true for a past date`() {
        // Arrange: A clearly past date is always valid for querying
        val pastDate = LocalDate.of(2020, 1, 1)

        // Act
        val result = dateChecker.checkValidDate(pastDate)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `checkValidDate returns false for a far future date`() {
        // Arrange: A date far in the future will never have price data available yet
        val farFutureDate = LocalDate.now().plusYears(1)

        // Act
        val result = dateChecker.checkValidDate(farFutureDate)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `checkValidDate result matches DateFormatter isValidQueryDate`() {
        // Arrange
        val pastDate = LocalDate.of(2022, 6, 15)

        // Act
        val checkerResult = dateChecker.checkValidDate(pastDate)
        val formatterResult = DateFormatter.isValidQueryDate(pastDate)

        // Assert: DateChecker delegates to DateFormatter so results must match
        assertEquals(formatterResult, checkerResult)
    }

    @Test
    fun `checkValidDate returns true for yesterday`() {
        // Arrange
        val yesterday = LocalDate.now().minusDays(1)

        // Act
        val result = dateChecker.checkValidDate(yesterday)

        // Assert
        assertTrue(result)
    }
}
