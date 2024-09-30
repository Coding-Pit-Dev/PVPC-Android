package com.codingpit.pvpcplanner.ui.main

import androidx.compose.runtime.Composable
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeVM

@Composable
fun MainScreen(
    viewModel: HomeVM,
) {
    HomeScreen(viewModel = viewModel)
    viewModel.getPrices()
}
