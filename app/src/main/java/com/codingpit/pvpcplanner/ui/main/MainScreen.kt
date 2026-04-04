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
import com.codingpit.pvpcplanner.utils.Constants

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    deviceAddViewModel: DeviceAddViewModel,
    settingsViewModel: SettingsViewModel,
    statsViewModel: StatsViewModel,
) {
    var selectedScreen by remember { mutableStateOf(Constants.HOME_SCREEN) }
    var deviceToEdit by remember { mutableStateOf<Device?>(null) }

    val title =
        when (selectedScreen) {
            Constants.HOME_SCREEN -> stringResource(R.string.nav_prices)
            Constants.DEVICES_SCREEN -> stringResource(R.string.nav_devices)
            Constants.DEVICE_ADD_SCREEN -> null
            Constants.STATS_SCREEN -> stringResource(R.string.nav_stats)
            Constants.SETTINGS_SCREEN -> stringResource(R.string.nav_settings)
            else -> selectedScreen
        }

    val showBottomBar = selectedScreen != Constants.DEVICE_ADD_SCREEN

    ScaffoldScreen(
        title = title,
        fab = {
            when (selectedScreen) {
                Constants.HOME_SCREEN -> {}
                Constants.DEVICES_SCREEN -> {
                    FloatingActionButton(onClick = {
                        deviceToEdit = null
                        selectedScreen = Constants.DEVICE_ADD_SCREEN
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
                selectedScreen = Constants.DEVICE_ADD_SCREEN
            },
            onNavigateToDeviceEdit = { device ->
                deviceToEdit = device
                selectedScreen = Constants.DEVICE_ADD_SCREEN
            },
            onNavigateBack = {
                deviceToEdit = null
                selectedScreen = Constants.DEVICES_SCREEN
            },
        )
    }
}
