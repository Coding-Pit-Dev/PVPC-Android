package com.codingpit.pvpcplanner.data.local.store

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceThresholdMapperTest {
    @Test
    fun `toMilliEurosPerKwh converts decimal threshold to fixed point`() {
        assertEquals(123, 0.123f.toMilliEurosPerKwh())
        assertEquals(219, 0.219f.toMilliEurosPerKwh())
    }

    @Test
    fun `toMilliEurosPerKwh clamps negative values`() {
        assertEquals(0, (-1f).toMilliEurosPerKwh())
    }

    @Test
    fun `toPriceThresholdEurosPerKwh converts fixed point to decimal`() {
        assertEquals(0.123f, 123.toPriceThresholdEurosPerKwh())
        assertEquals(0f, 0.toPriceThresholdEurosPerKwh())
    }
}
