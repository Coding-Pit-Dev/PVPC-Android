package com.codingpit.pvpcplanner.data.mappers

import com.codingpit.pvpcplanner.PvpcSettings
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.TimeFormat

fun PvpcSettings.DarkMode.toDomain() =
    when (this) {
        PvpcSettings.DarkMode.SYSTEM -> DarkMode.SYSTEM
        PvpcSettings.DarkMode.LIGHT -> DarkMode.LIGHT
        PvpcSettings.DarkMode.DARK -> DarkMode.DARK
        PvpcSettings.DarkMode.UNRECOGNIZED -> DarkMode.SYSTEM
    }

fun DarkMode.toProto() =
    when (this) {
        DarkMode.SYSTEM -> PvpcSettings.DarkMode.SYSTEM
        DarkMode.LIGHT -> PvpcSettings.DarkMode.LIGHT
        DarkMode.DARK -> PvpcSettings.DarkMode.DARK
    }

fun PvpcSettings.TimeFormat.toDomain() =
    when (this) {
        PvpcSettings.TimeFormat.AM_PM -> TimeFormat.TWELVE_HOURS
        PvpcSettings.TimeFormat.FULL_HOURS -> TimeFormat.TWENTY_FOUR_HOURS
        PvpcSettings.TimeFormat.UNRECOGNIZED -> TimeFormat.TWELVE_HOURS
    }

fun TimeFormat.toProto() =
    when (this) {
        TimeFormat.TWELVE_HOURS -> PvpcSettings.TimeFormat.AM_PM
        TimeFormat.TWENTY_FOUR_HOURS -> PvpcSettings.TimeFormat.FULL_HOURS
    }
