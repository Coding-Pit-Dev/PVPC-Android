package com.codingpit.pvpcplanner.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.ui.components.ScaffoldScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.main.components.MainBottomBarNav
import com.codingpit.pvpcplanner.ui.main.components.MainContent

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel
) {
    var selectedScreen by remember { mutableStateOf("Home") }

    ScaffoldScreen(
        modifier = Modifier.fillMaxSize(),
        title = selectedScreen,
        fab = {
            when (selectedScreen) {
                "Home" -> {}
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
                selectedIconColor = colorResource(id = R.color.white),
                unselectedIconColor = Color.LightGray
            )
        }
    ) {
        MainContent(
            homeViewModel = homeViewModel,
            devicesViewModel = devicesViewModel,
            selectedScreen = selectedScreen
        )
    }
}



