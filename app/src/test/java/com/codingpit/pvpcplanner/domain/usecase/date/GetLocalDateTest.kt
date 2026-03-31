package com.codingpit.pvpcplanner.domain.usecase.date

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetLocalDateTest {
    private lateinit var getLocalDate: GetLocalDate

    @Before
    fun setup() {
        getLocalDate = GetLocalDate()
    }

    @Test
    fun `invoke returns a non-null LocalDate`() {
        // Act
        val result = getLocalDate()

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `invoke returns a LocalDate with a plausible year`() {
        // Act
        val result = getLocalDate()

        // Assert: year should be at least 2024 (test written in 2026)
        assertTrue("Expected year >= 2024 but was ${result.year}", result.year >= 2024)
    }

    @Test
    fun `invoke returns a date that is not in the far future`() {
        // Act
        val result = getLocalDate()

        // Assert: result should not be more than 2 days in the future to guard against oddities
        val twoDaysFromNow = LocalDate.now().plusDays(2)
        assertTrue(
            "Expected date <= $twoDaysFromNow but was $result",
            !result.isAfter(twoDaysFromNow),
        )
    }

    @Test
    fun `invoke returns a date that is not in the past`() {
        // Act
        val result = getLocalDate()

        // Assert: current date should be today or at most yesterday (accounting for time zones)
        val yesterday = LocalDate.now().minusDays(1)
        assertTrue(
            "Expected date >= $yesterday but was $result",
            !result.isBefore(yesterday),
        )
    }
}
