package com.codingpit.pvpcplanner.domain.models

data class Settings(
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOURS,
    val yAxisSlots: Int = 5,
    val priceThreshold: Float = 0f,
)

enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM,
}

enum class TimeFormat {
    TWELVE_HOURS,
    TWENTY_FOUR_HOURS,
}
