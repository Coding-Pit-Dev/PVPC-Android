package com.codingpit.pvpcplanner.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings

object Constants {

    const val HOME_SCREEN = "Prices"
    const val DEVICES_SCREEN = "Devices"
    const val SETTINGS_SCREEN = "Settings"

    val ROUTES_NAVIGATION_BOTTOM_BAR = mapOf(
        HOME_SCREEN to Icons.Default.Home,
        DEVICES_SCREEN to Icons.Default.Build,
        SETTINGS_SCREEN to Icons.Default.Settings,
    )
}