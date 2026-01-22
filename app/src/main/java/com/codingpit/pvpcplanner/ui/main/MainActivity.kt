package com.codingpit.pvpcplanner.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.ui.devices.DevicesViewModel
import com.codingpit.pvpcplanner.ui.home.HomeViewModel
import com.codingpit.pvpcplanner.ui.settings.SettingsViewModel
import com.codingpit.pvpcplanner.ui.theme.PVPCPlannerTheme
import com.codingpit.pvpcplanner.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val devicesViewModel by viewModels<DevicesViewModel>()
    private val settingsViewModel by viewModels<SettingsViewModel>()

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by themeManager.getSettings().collectAsStateWithLifecycle(Settings())

            PVPCPlannerTheme(darkTheme = themeManager.shouldShowDarkTheme(settings.darkMode)) {
                MainScreen(homeViewModel, devicesViewModel, settingsViewModel)
            }
        }
    }
}
