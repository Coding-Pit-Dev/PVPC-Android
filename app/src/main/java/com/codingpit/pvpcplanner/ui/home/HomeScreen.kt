package com.codingpit.pvpcplanner.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.ui.components.PricesListComponent

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
        is HomeState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }

        is HomeState.Success -> {
            val responseData = state.pvpcEntries
            HomeComponents(
                modifier = modifier,
                pvpcEntries = responseData,
                date = state.currentDate,
                nextDayEnabled = state.nextDateEnabled,
                onPreviewClicked = { viewModel.onPreviewClicked() },
                onNextClicked = { viewModel.onNextClicked() }
            )
        }

        is HomeState.Error -> {
        }
    }
}

@Composable
fun HomeComponents(
    modifier: Modifier,
    date: String,
    pvpcEntries: List<PVPCModel>,
    nextDayEnabled: Boolean,
    onPreviewClicked: () -> Unit = { },
    onNextClicked: () -> Unit = { }
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                date = date,
                nextDayEnabled = nextDayEnabled,
                onPreviewClicked = onPreviewClicked,
                onNextClicked = onNextClicked
            )
            PricesListComponent(pvpcEntries)
        }
    }
}

@Composable
private fun DateSelector(
    modifier: Modifier = Modifier,
    date: String,
    nextDayEnabled: Boolean,
    onPreviewClicked: () -> Unit,
    onNextClicked: () -> Unit
) {

    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        IconButton(modifier = Modifier, onClick = onPreviewClicked) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Preview")
        }
        Text(date)
        IconButton(modifier = Modifier, enabled = nextDayEnabled, onClick = onNextClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = "Preview"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DateSelector_Preview() {
    DateSelector(
        modifier = Modifier.fillMaxWidth(),
        date = "2023-09-01",
        nextDayEnabled = true,
        onPreviewClicked = { },
        onNextClicked = { })
}
