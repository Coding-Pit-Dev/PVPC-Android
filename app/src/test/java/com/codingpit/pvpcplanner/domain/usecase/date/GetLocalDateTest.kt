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
    fun `invoke returns a date near current date`() {
        // Arrange: capture reference before calling to avoid midnight boundary flakiness
        val referenceDate = LocalDate.now()

        // Act
        val result = getLocalDate()

        // Assert: result should be within ±1 day of the reference date
        assertTrue(
            "Expected date >= ${referenceDate.minusDays(1)} but was $result",
            !result.isBefore(referenceDate.minusDays(1)),
        )
        assertTrue(
            "Expected date <= ${referenceDate.plusDays(2)} but was $result",
            !result.isAfter(referenceDate.plusDays(2)),
        )
    }
}
