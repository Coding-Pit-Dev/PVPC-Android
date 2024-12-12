package com.codingpit.pvpcplanner.ui.main.MainComponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codingpit.pvpcplanner.ui.home.HomeScreen
import com.codingpit.pvpcplanner.ui.home.HomeVM

@Composable
fun MainContent(
    modifier: Modifier,
    viewModel: HomeVM
) {
    HomeScreen(
        modifier = modifier,
        viewModel = viewModel,
    )
}
