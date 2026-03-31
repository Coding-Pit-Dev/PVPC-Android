package com.codingpit.pvpcplanner.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.ui.components.PricesComponent

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
        is HomeState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is HomeState.Success -> {
            HomeComponents(
                pvpcEntries = state.pvpcEntries,
                selectedDate = state.selectedDate,
                nextDayEnabled = state.nextDateEnabled,
                currentPrice = state.currentPrice,
                currentHour = state.currentHour,
                currentDate = state.currentDate,
                timeFormat = state.timeFormat,
                onPreviousClicked = { viewModel.onPreviousClicked() },
                onNextClicked = { viewModel.onNextClicked() },
            )
        }

        is HomeState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error)
            }
        }
    }
}

@Composable
fun HomeComponents(
    selectedDate: String,
    currentDate: String,
    pvpcEntries: List<PVPCModel>,
    currentPrice: Double,
    currentHour: Int,
    nextDayEnabled: Boolean,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOURS,
    onPreviousClicked: () -> Unit = { },
    onNextClicked: () -> Unit = { },
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PricesComponent(
                pvpcEntries = pvpcEntries,
                selectedDate = selectedDate,
                currentDate = currentDate,
                currentPrice = currentPrice,
                currentHour = currentHour,
                nextDayEnabled = nextDayEnabled,
                onPreviousClicked = onPreviousClicked,
                onNextClicked = onNextClicked,
                timeFormat = timeFormat,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}
