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
}
