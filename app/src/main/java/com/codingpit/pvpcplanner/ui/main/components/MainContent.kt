package com.codingpit.pvpcplanner.ui.main.components

import androidx.compose.runtime.Composable
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.ui.devices.DeviceAddScreen
import com.codingpit.pvpcplanner.ui.devices.DeviceAddViewModel
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.settings.SettingsScreen
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.ui.stats.StatsScreen
import com.codingpit.pvpcplanner.ui.stats.StatsViewModel
import com.codingpit.pvpcplanner.utils.Constants.DEVICES_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.DEVICE_ADD_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.HOME_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.SETTINGS_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.STATS_SCREEN

@Composable
fun MainContent(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    deviceAddViewModel: DeviceAddViewModel,
    settingsViewModel: SettingsViewModel,
    statsViewModel: StatsViewModel,
    selectedScreen: String,
    deviceToEdit: Device? = null,
    onNavigateToDeviceAdd: () -> Unit = {},
    onNavigateToDeviceEdit: (Device) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    when (selectedScreen) {
        HOME_SCREEN ->
            HomeScreen(
                viewModel = homeViewModel,
            )

        DEVICES_SCREEN ->
            DevicesScreen(
                viewModel = devicesViewModel,
                onDeviceClick = onNavigateToDeviceEdit,
            )

        DEVICE_ADD_SCREEN ->
            DeviceAddScreen(
                viewModel = deviceAddViewModel,
                device = deviceToEdit,
                onBackClick = onNavigateBack,
            )

        SETTINGS_SCREEN ->
            SettingsScreen(viewModel = settingsViewModel)

        STATS_SCREEN ->
            StatsScreen(viewModel = statsViewModel)
    }
}
