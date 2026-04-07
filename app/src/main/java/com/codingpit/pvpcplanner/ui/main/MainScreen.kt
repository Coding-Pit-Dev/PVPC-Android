package com.codingpit.pvpcplanner.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.ui.components.ScaffoldScreen
import com.codingpit.pvpcplanner.ui.devices.DeviceAddViewModel
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.main.components.MainBottomBarNav
import com.codingpit.pvpcplanner.ui.main.components.MainContent
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.ui.stats.StatsViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    deviceAddViewModel: DeviceAddViewModel,
    settingsViewModel: SettingsViewModel,
    statsViewModel: StatsViewModel,
) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var deviceToEdit by remember { mutableStateOf<Device?>(null) }

    val title =
        when (selectedScreen) {
            is Screen.Home -> stringResource(R.string.nav_prices)
            is Screen.Devices -> stringResource(R.string.nav_devices)
            is Screen.DeviceAdd -> null
            is Screen.Stats -> stringResource(R.string.nav_stats)
            is Screen.Settings -> stringResource(R.string.nav_settings)
        }

    val showBottomBar = selectedScreen !is Screen.DeviceAdd

    ScaffoldScreen(
        title = title,
        fab = {
            when (selectedScreen) {
                is Screen.Home -> {}
                is Screen.Devices -> {
                    FloatingActionButton(onClick = {
                        deviceToEdit = null
                        selectedScreen = Screen.DeviceAdd
                    }) {
                        Icon(Icons.Filled.Add, stringResource(R.string.action_add))
                    }
                }

                else -> {}
            }
        },
        bottomBar = {
            if (showBottomBar) {
                MainBottomBarNav(
                    selectedScreen = selectedScreen,
                    onScreenSelected = { screen -> selectedScreen = screen },
                )
            }
        },
    ) {
        MainContent(
            homeViewModel = homeViewModel,
            devicesViewModel = devicesViewModel,
            deviceAddViewModel = deviceAddViewModel,
            settingsViewModel = settingsViewModel,
            statsViewModel = statsViewModel,
            selectedScreen = selectedScreen,
            deviceToEdit = deviceToEdit,
            onNavigateToDeviceAdd = {
                deviceToEdit = null
                selectedScreen = Screen.DeviceAdd
            },
            onNavigateToDeviceEdit = { device ->
                deviceToEdit = device
                selectedScreen = Screen.DeviceAdd
            },
            onNavigateBack = {
                deviceToEdit = null
                selectedScreen = Screen.Devices
            },
        )
    }
}
