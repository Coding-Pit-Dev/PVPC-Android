package com.codingpit.pvpcplanner.ui.main.MainComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codingpit.pvpcplanner.ui.devices.DevicesScreen
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.utils.Constants.HOME_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.DEVICES_SCREEN
import com.codingpit.pvpcplanner.utils.Constants.SETTING_SCREEN
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeViewModel

@Composable
fun MainContent(
    modifier: Modifier,
    viewModel: HomeViewModel,
    devicesViewModel: DevicesViewModel,
    selectedScreen: String,
) {

    when (selectedScreen) {
        HOME_SCREEN ->
            Box(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    modifier = modifier,
                    viewModel = viewModel,
                )
            }

        DEVICES_SCREEN ->
            DevicesScreen(devicesViewModel)

        SETTING_SCREEN ->
            Text(
                text = SETTING_SCREEN
            )
    }
}
