package com.codingpit.pvpcplanner.ui.main.components

import androidx.compose.runtime.Composable
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.settings.SettingsScreen
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.utils.Constants.DEVICES_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.HOME_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.SETTINGS_SCREEN

@Composable
fun MainContent(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    settingsViewModel: SettingsViewModel,
    selectedScreen: String,
) {
    when (selectedScreen) {
        HOME_SCREEN ->
            HomeScreen(
                viewModel = homeViewModel,
            )

        DEVICES_SCREEN ->
            DevicesScreen(viewModel = devicesViewModel)

        SETTINGS_SCREEN ->
            SettingsScreen(viewModel = settingsViewModel)
    }
}
