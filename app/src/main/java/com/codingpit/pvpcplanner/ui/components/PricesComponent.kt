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
import androidx.compose.runtime.LaunchedEffect
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
import com.codingpit.pvpcplanner.ui.home.DateSelector
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlin.math.roundToInt


@Composable
fun PricesComponent(
    responseData: List<PVPCModel>,
    selectedDate: String,
    currentPrice: Double,
    currentHour: Int,
    currentDate: String,
    nextDayEnabled: Boolean,
    onPreviewClicked: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyListState()

    var selectedPrice by remember(responseData) {
        mutableDoubleStateOf(currentPrice)
    }

    var selectedHour by remember(responseData) {
        mutableIntStateOf(currentHour)
    }

    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CurrentPriceLabel(
                price = selectedPrice,
                hour = selectedHour,
                currentHour = currentHour,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            PriceChart(responseData, modifier = Modifier.padding(horizontal = 16.dp)) { x, y ->
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
                onNextClicked = onNextClicked
            )
        }

        items(
            items = responseData,
            key = {
                it.startHour
            }
        ) { pvpcItem ->
            PriceCardComponent(
                hour = "${pvpcItem.startHour} - ${pvpcItem.endHour}",
                price = pvpcItem.pcb
            )
        }
    }
}

@Composable
private fun CurrentPriceLabel(price: Double, hour: Int, currentHour: Int, modifier: Modifier = Modifier) {

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
            lineHeight = 24.sp
        )
        Text(
            text = price.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 40.sp
        )
    }

}

@Composable
fun PriceChart(
    responseData: List<PVPCModel>,
    modifier: Modifier = Modifier,
    onMarkerChanged: (Int, Double) -> Unit,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(responseData) {
        modelProducer.runTransaction {
            lineSeries { series(responseData.map { it.pcb.toFloat() }) }
        }
    }

    val yAxisStep = rememberYAxis(responseData.map { it.pcb.toFloat() })

    CartesianChartHost(
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            marker = rememberDefaultCartesianMarker(
                label = TextComponent(),
                labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,

                ),
            markerVisibilityListener = object : CartesianMarkerVisibilityListener {
                override fun onUpdated(
                    marker: CartesianMarker,
                    targets: List<CartesianMarker.Target>
                ) {
                    super.onUpdated(marker, targets)
                    val target = targets.first()
                    onMarkerChanged(
                        target.x.toInt(),
                        responseData.first { it.startHour == target.x.toInt() }.pcb
                    )
                }
            },
            startAxis = VerticalAxis.rememberStart(
                guideline = null,
                itemPlacer = VerticalAxis.ItemPlacer.step(step = { yAxisStep.toDouble() })
            ),
            bottomAxis = HorizontalAxis.rememberBottom(guideline = null),
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modelProducer = modelProducer,
    )
}

@Composable
private fun rememberYAxis(values: List<Float>) = remember(values) {
    if (values.isEmpty()) {
        0.1f // Default step if no data
    } else {
        val minPrice = values.minOrNull() ?: 0f
        val maxPrice = values.maxOrNull() ?: 1f // Avoid division by zero if all prices are same

        val range = maxPrice - minPrice
        if (range == 0f) {
            0.1f // Handle case where all prices are the same
        } else {
            val desiredLabelCount = 5 // Adjust as needed
            val calculatedStep = range / (desiredLabelCount - 1)
            // Optional: Round to a nicer number, e.g., nearest 0.01 or 0.05
            // This is a simple example; you might want more sophisticated rounding
            (calculatedStep * 100).roundToInt() / 100f // Round to 2 decimal places
                .coerceAtLeast(0.01f) // Ensure step is not too small
        }
    }
}


@Preview
@Composable
private fun PricesView_Preview() {
    PricesComponent(
        responseData = List(24) { 
            PVPCModel(
                startHour = it,
                endHour = it + 1,
                pcb = it.toDouble(),
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
        currentDate = "2023-09-01"
    )
}
