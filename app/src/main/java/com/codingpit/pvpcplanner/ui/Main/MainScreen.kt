package com.codingpit.pvpcplanner.ui.Main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codingpit.pvpcplanner.ui.Home.HomeScreen
import com.codingpit.pvpcplanner.ui.Home.HomeVM

@Composable
fun MainScreen(
    viewModel: HomeVM,
) {
    HomeScreen(viewModel = viewModel)
    viewModel.getHeroes()

}

@Preview
@Composable
private fun MainScreen_Preview() {
    //MainScreen()
}