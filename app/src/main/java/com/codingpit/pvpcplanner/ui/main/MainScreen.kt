package com.codingpit.pvpcplanner.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.main.MainComponents.MainBottomBarNav
import com.codingpit.pvpcplanner.ui.main.MainComponents.MainContent

@Composable
fun MainScreen(
    viewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel
) {
    var selectedScreen by remember { mutableStateOf("Home") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainBottomBarNav(
                selectedScreen = selectedScreen,
                onScreenSelected = { screen -> selectedScreen = screen },
                selectedIconColor = colorResource(id = R.color.white),
                unselectedIconColor = Color.LightGray
            )
        }
    ) { paddingValues ->
        MainContent(
            modifier = Modifier.padding(paddingValues),
            viewModel,
            devicesViewModel,
            selectedScreen
        )
    }
}



