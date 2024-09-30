package com.codingpit.pvpcplanner.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.codingpit.pvpcplanner.ui.home.HomeVM
import com.codingpit.pvpcplanner.ui.theme.PVPCPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeVM by viewModels<HomeVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PVPCPlannerTheme {

                MainScreen(
                    homeVM,
                )
            }
        }
    }
}


