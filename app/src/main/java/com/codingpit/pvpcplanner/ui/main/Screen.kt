package com.codingpit.pvpcplanner.ui.main

sealed class Screen {
    object Home : Screen()
    object Devices : Screen()
    object DeviceAdd : Screen()
    object Stats : Screen()
    object Settings : Screen()
}
