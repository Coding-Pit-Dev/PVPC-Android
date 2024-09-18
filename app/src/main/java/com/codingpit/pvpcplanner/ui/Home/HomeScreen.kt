package com.codingpit.pvpcplanner.ui.Home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.codingpit.pvpcplanner.domains.models.PVPCDTO
import com.codingpit.pvpcplanner.ui.components.PricesListComponent

@Composable
fun HomeScreen(
    viewModel: HomeVM
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is HomeState.Loading -> {
            Log.d("HomeScreen", "Loading...")
        }

        is HomeState.Success -> {
            val responseData = (state as HomeState.Success).data
            Log.d("HomeScreen", "Success: $responseData")
            HomeComponents(responseData)
        }

        is HomeState.Error -> {
            val error = (state as HomeState.Error).error
            Log.d("HomeScreen", "Error: $error")
        }
    }
}

@Composable
fun HomeComponents(
    responseData: List<PVPCDTO>
) {
    PricesListComponent(responseData)
}
