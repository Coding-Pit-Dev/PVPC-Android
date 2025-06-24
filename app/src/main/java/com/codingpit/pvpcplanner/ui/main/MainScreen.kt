package com.codingpit.pvpcplanner.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codingpit.pvpcplanner.ui.components.ScaffoldScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.main.components.MainBottomBarNav
import com.codingpit.pvpcplanner.ui.main.components.MainContent
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    settingsViewModel: SettingsViewModel,
) {
    var selectedScreen by remember { mutableStateOf("Prices") }

    ScaffoldScreen(
        title = selectedScreen,
        fab = {
            when (selectedScreen) {
                "Prices" -> {}
                "Devices" -> {
                    FloatingActionButton(onClick = {
                        devicesViewModel.showAddDeviceModal()
                    }) {
                        Icon(Icons.Filled.Add, "Floating action button.")
                    }
                }

                else -> {}
            }
        },
        bottomBar = {
            MainBottomBarNav(
                selectedScreen = selectedScreen,
                onScreenSelected = { screen -> selectedScreen = screen },
            )
        },
    ) {
        MainContent(
            homeViewModel = homeViewModel,
            devicesViewModel = devicesViewModel,
            settingsViewModel = settingsViewModel,
            selectedScreen = selectedScreen,
        )
    }
}
