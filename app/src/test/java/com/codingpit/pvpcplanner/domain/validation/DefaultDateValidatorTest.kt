package com.codingpit.pvpcplanner.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DefaultDateValidatorTest {
    private lateinit var dateValidator: DefaultDateValidator

    @Before
    fun setup() {
        dateValidator = DefaultDateValidator()
    }

    @Test
    fun `validateDateRange returns Success for date within range`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 15)
        val startDate = LocalDate.of(2023, 10, 10)
        val endDate = LocalDate.of(2023, 10, 20)

        // Act
        val result = dateValidator.validateDateRange(date, startDate, endDate)

        // Assert
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateDateRange returns Error for date before range`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 5)
        val startDate = LocalDate.of(2023, 10, 10)
        val endDate = LocalDate.of(2023, 10, 20)

        // Act
        val result = dateValidator.validateDateRange(date, startDate, endDate)

        // Assert
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertTrue(error.reason.contains("before the allowed range"))
        assertEquals("DATE_TOO_EARLY", error.errorCode)
    }

    @Test
    fun `validateDateRange returns Error for date after range`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 25)
        val startDate = LocalDate.of(2023, 10, 10)
        val endDate = LocalDate.of(2023, 10, 20)

        // Act
        val result = dateValidator.validateDateRange(date, startDate, endDate)

        // Assert
        assertTrue(result is ValidationResult.Error)
        val error = result as ValidationResult.Error
        assertTrue(error.reason.contains("after the allowed range"))
        assertEquals("DATE_TOO_LATE", error.errorCode)
    }

    @Test
    fun `validateDateRange returns Success for boundary dates`() {
        // Arrange
        val startDate = LocalDate.of(2023, 10, 10)
        val endDate = LocalDate.of(2023, 10, 20)

        // Act
        val resultStart = dateValidator.validateDateRange(startDate, startDate, endDate)
        val resultEnd = dateValidator.validateDateRange(endDate, startDate, endDate)

        // Assert
        assertTrue(resultStart is ValidationResult.Success)
        assertTrue(resultEnd is ValidationResult.Success)
    }

    @Test
    fun `validateNotTooFarInFuture returns Success for reasonable future date`() {
        // Arrange - Use a date that should be within 7 days from now
        val futureDate = LocalDate.now().plusDays(3)

        // Act
        val result = dateValidator.validateNotTooFarInFuture(futureDate)

        // Assert
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateNotTooFarInFuture returns Success for current date`() {
        // Arrange
        val currentDate = LocalDate.now()

        // Act
        val result = dateValidator.validateNotTooFarInFuture(currentDate)

        // Assert
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `validateNotTooFarInFuture returns Success for past date`() {
        // Arrange
        val pastDate = LocalDate.now().minusDays(5)

        // Act
        val result = dateValidator.validateNotTooFarInFuture(pastDate)

        // Assert
        assertTrue(result is ValidationResult.Success)
    }
}
