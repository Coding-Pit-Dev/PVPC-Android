package com.codingpit.pvpcplanner.domain.usecase.date

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetLocalHourTest {
    private lateinit var getLocalHour: GetLocalHour

    @Before
    fun setup() {
        getLocalHour = GetLocalHour()
    }

    @Test
    fun `invoke returns a value in the valid hour range 0 to 23`() {
        // Act
        val result = getLocalHour()

        // Assert
        assertTrue(
            "Expected hour in 0..23 but was $result",
            result in 0..23,
        )
    }

    @Test
    fun `invoke returns an integer`() {
        // Act
        val result = getLocalHour()

        // Assert: result is an Int (compile-time guarantee, but we also verify range)
        assertTrue(result >= 0)
        assertTrue(result <= 23)
    }

    @Test
    fun `invoke returns a non-negative hour`() {
        // Act
        val result = getLocalHour()

        // Assert
        assertTrue("Hour must not be negative, was $result", result >= 0)
    }

    @Test
    fun `invoke returns an hour less than 24`() {
        // Act
        val result = getLocalHour()

        // Assert
        assertTrue("Hour must be less than 24, was $result", result < 24)
    }
}
