package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.PvpcSettings
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsMapperTest {
    @Test
    fun `DarkMode proto SYSTEM maps to domain SYSTEM`() {
        assertEquals(DarkMode.SYSTEM, PvpcSettings.DarkMode.SYSTEM.toDomain())
    }

    @Test
    fun `DarkMode proto LIGHT maps to domain LIGHT`() {
        assertEquals(DarkMode.LIGHT, PvpcSettings.DarkMode.LIGHT.toDomain())
    }

    @Test
    fun `DarkMode proto DARK maps to domain DARK`() {
        assertEquals(DarkMode.DARK, PvpcSettings.DarkMode.DARK.toDomain())
    }

    @Test
    fun `DarkMode proto UNRECOGNIZED falls back to SYSTEM`() {
        assertEquals(DarkMode.SYSTEM, PvpcSettings.DarkMode.UNRECOGNIZED.toDomain())
    }

    @Test
    fun `DarkMode domain SYSTEM maps to proto SYSTEM`() {
        assertEquals(PvpcSettings.DarkMode.SYSTEM, DarkMode.SYSTEM.toProto())
    }

    @Test
    fun `DarkMode domain LIGHT maps to proto LIGHT`() {
        assertEquals(PvpcSettings.DarkMode.LIGHT, DarkMode.LIGHT.toProto())
    }

    @Test
    fun `DarkMode domain DARK maps to proto DARK`() {
        assertEquals(PvpcSettings.DarkMode.DARK, DarkMode.DARK.toProto())
    }

    @Test
    fun `TimeFormat proto AM_PM maps to domain TWELVE_HOURS`() {
        assertEquals(TimeFormat.TWELVE_HOURS, PvpcSettings.TimeFormat.AM_PM.toDomain())
    }

    @Test
    fun `TimeFormat proto FULL_HOURS maps to domain TWENTY_FOUR_HOURS`() {
        assertEquals(TimeFormat.TWENTY_FOUR_HOURS, PvpcSettings.TimeFormat.FULL_HOURS.toDomain())
    }

    @Test
    fun `TimeFormat proto UNRECOGNIZED falls back to TWELVE_HOURS`() {
        assertEquals(TimeFormat.TWELVE_HOURS, PvpcSettings.TimeFormat.UNRECOGNIZED.toDomain())
    }

    @Test
    fun `TimeFormat domain TWELVE_HOURS maps to proto AM_PM`() {
        assertEquals(PvpcSettings.TimeFormat.AM_PM, TimeFormat.TWELVE_HOURS.toProto())
    }

    @Test
    fun `TimeFormat domain TWENTY_FOUR_HOURS maps to proto FULL_HOURS`() {
        assertEquals(PvpcSettings.TimeFormat.FULL_HOURS, TimeFormat.TWENTY_FOUR_HOURS.toProto())
    }

    @Test
    fun `DarkMode round-trip SYSTEM`() {
        assertEquals(DarkMode.SYSTEM, DarkMode.SYSTEM.toProto().toDomain())
    }

    @Test
    fun `DarkMode round-trip DARK`() {
        assertEquals(DarkMode.DARK, DarkMode.DARK.toProto().toDomain())
    }

    @Test
    fun `TimeFormat round-trip TWENTY_FOUR_HOURS`() {
        assertEquals(TimeFormat.TWENTY_FOUR_HOURS, TimeFormat.TWENTY_FOUR_HOURS.toProto().toDomain())
    }
}
