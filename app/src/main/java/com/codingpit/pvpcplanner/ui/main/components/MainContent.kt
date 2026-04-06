package com.codingpit.pvpcplanner.ui.main.components

import androidx.compose.runtime.Composable
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.ui.devices.DeviceAddScreen
import com.codingpit.pvpcplanner.ui.devices.DeviceAddViewModel
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.main.Screen
import com.codingpit.pvpcplanner.ui.settings.SettingsScreen
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.ui.stats.StatsScreen
import com.codingpit.pvpcplanner.ui.stats.StatsViewModel

@Composable
fun MainContent(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    deviceAddViewModel: DeviceAddViewModel,
    settingsViewModel: SettingsViewModel,
    statsViewModel: StatsViewModel,
    selectedScreen: Screen,
    deviceToEdit: Device? = null,
    onNavigateToDeviceAdd: () -> Unit = {},
    onNavigateToDeviceEdit: (Device) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    when (selectedScreen) {
        is Screen.Home ->
            HomeScreen(
                viewModel = homeViewModel,
            )

        is Screen.Devices ->
            DevicesScreen(
                viewModel = devicesViewModel,
                onDeviceClick = onNavigateToDeviceEdit,
            )

        is Screen.DeviceAdd ->
            DeviceAddScreen(
                viewModel = deviceAddViewModel,
                device = deviceToEdit,
                onBackClick = onNavigateBack,
            )

        is Screen.Settings ->
            SettingsScreen(viewModel = settingsViewModel)

        is Screen.Stats ->
            StatsScreen(viewModel = statsViewModel)
    }
}
