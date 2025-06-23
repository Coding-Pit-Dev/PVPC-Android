package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.ui.components.graph.PriceChart
import com.codingpit.pvpcplanner.ui.home.DateSelector

@Composable
fun PricesComponent(
    pvpcEntries: List<PVPCModel>,
    selectedDate: String,
    currentPrice: Double,
    currentHour: Int,
    currentDate: String,
    nextDayEnabled: Boolean,
    onPreviewClicked: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOURS,
) {
    val state = rememberLazyListState()

    var selectedPrice by remember(pvpcEntries) {
        mutableDoubleStateOf(currentPrice)
    }

    var selectedHour by remember(pvpcEntries) {
        mutableIntStateOf(currentHour)
    }

    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            CurrentPriceLabel(
                price = selectedPrice,
                hour = selectedHour,
                currentHour = currentHour,
                modifier = Modifier.padding(16.dp),
            )
        }

        item {
            PriceChart(
                responseData = pvpcEntries,
                timeFormat = timeFormat,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) { x, y ->
                selectedPrice = y
                selectedHour = x
            }
        }

        stickyHeader {
            DateSelector(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = selectedDate,
                currentDate = currentDate,
                nextDayEnabled = nextDayEnabled,
                onPreviewClicked = onPreviewClicked,
                onNextClicked = onNextClicked,
            )
        }

        items(
            items = pvpcEntries,
            key = {
                it.startHour
            },
        ) { pvpcItem ->
            PriceCardComponent(
                hour = "${pvpcItem.startHour} - ${pvpcItem.endHour}",
                price = pvpcItem.pcb,
            )
        }
    }
}

@Composable
private fun CurrentPriceLabel(
    price: Double,
    hour: Int,
    currentHour: Int,
    modifier: Modifier = Modifier,
) {
    val hourText = if (hour == currentHour) {
        stringResource(R.string.current_hour_template).format(hour)
    } else {
        hour.toString()
    }

    Column(modifier = modifier) {
        Text(
            text = hourText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 24.sp,
        )
        Text(
            text = price.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 40.sp,
        )
    }
}


@Preview
@Composable
private fun PricesView_Preview() {
    PricesComponent(
        pvpcEntries = List(24) {
            PVPCModel(
                startHour = it,
                endHour = it + 1,
                pcb = (it*0.01).toDouble(),
                day = "2023-09-01",
                cym = it.toDouble(),
            )
        },
        selectedDate = "2023-09-01",
        nextDayEnabled = true,
        onPreviewClicked = { },
        onNextClicked = { },
        currentPrice = 0.0,
        currentHour = 0,
        currentDate = "2023-09-01",
    )
}
