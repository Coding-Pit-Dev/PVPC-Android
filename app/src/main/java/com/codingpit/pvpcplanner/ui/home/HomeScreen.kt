package com.codingpit.pvpcplanner.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.codingpit.pvpcplanner.domains.models.PVPCModel
import com.codingpit.pvpcplanner.ui.components.PricesListComponent

@Composable
fun HomeScreen(
    modifier: Modifier,
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
            HomeComponents(
                modifier = modifier,
                responseData
            )
        }

        is HomeState.Error -> {
            val error = (state as HomeState.Error).error
            Log.d("HomeScreen", "Error: $error")
        }
    }
}

@Composable
fun HomeComponents(
    modifier: Modifier,
    responseData: List<PVPCModel>
) {
    Box( modifier = modifier.fillMaxSize()) {
        PricesListComponent(responseData)
    }
}
