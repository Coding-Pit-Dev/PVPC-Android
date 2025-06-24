package com.codingpit.pvpcplanner.domain.models

/*
data class Settings(
    val groups: List<SettingGroup> = emptyList(),
)

data class SettingGroup(
    val title: String,
    val settings: List<Setting>,
)

data class Setting(
    val title: String,
    val subtitle: String,
)
 */

data class Settings(
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOURS,
    val yAxisSlots: Int = 5,
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
