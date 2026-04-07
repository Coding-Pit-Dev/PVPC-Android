package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.domain.validation.DateValidator
import com.codingpit.pvpcplanner.domain.validation.ValidationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class IsValidDateTest {
    private val mockDateValidator = mockk<DateValidator>()
    private lateinit var isValidDate: IsValidDate

    @Before
    fun setup() {
        isValidDate = IsValidDate(mockDateValidator)
    }

    @Test
    fun `invoke returns true when validator returns Success`() {
        // Arrange
        val date = LocalDate.of(2023, 10, 15)
        every { mockDateValidator.validateQueryDate(date) } returns ValidationResult.Success

        // Act
        val result = isValidDate(date)

        // Assert
        assertTrue(result)
        verify(exactly = 1) { mockDateValidator.validateQueryDate(date) }
    }

    @Test
    fun `invoke returns false when validator returns Error`() {
        // Arrange
        val date = LocalDate.now().plusYears(1)
        every { mockDateValidator.validateQueryDate(date) } returns
            ValidationResult.Error(
                reason = "Price data not available",
                errorCode = "PRICE_DATA_NOT_AVAILABLE",
            )

        // Act
        val result = isValidDate(date)

        // Assert
        assertFalse(result)
        verify(exactly = 1) { mockDateValidator.validateQueryDate(date) }
    }

    @Test
    fun `invoke returns true for a past date when validator succeeds`() {
        // Arrange
        val pastDate = LocalDate.of(2020, 1, 1)
        every { mockDateValidator.validateQueryDate(pastDate) } returns ValidationResult.Success

        // Act
        val result = isValidDate(pastDate)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when validator error has no error code`() {
        // Arrange
        val date = LocalDate.now().plusDays(5)
        every { mockDateValidator.validateQueryDate(date) } returns
            ValidationResult.Error(reason = "Not available")

        // Act
        val result = isValidDate(date)

        // Assert
        assertFalse(result)
    }
}
