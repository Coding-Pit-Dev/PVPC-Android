package com.codingpit.pvpcplanner.ui.main.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.utils.Constants.DEVICES_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.HOME_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.SETTING_SCREEN

@Composable
fun MainContent(
    homeViewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    selectedScreen: String,
    modifier: Modifier = Modifier,
) {

    when (selectedScreen) {
        HOME_SCREEN ->
            HomeScreen(
                modifier = modifier,
                viewModel = homeViewModel,
            )

        DEVICES_SCREEN ->
            DevicesScreen(modifier = modifier, devicesViewModel = devicesViewModel)

        SETTING_SCREEN ->
            Text(
                text = SETTING_SCREEN
            )
    }
}
