package com.codingpit.pvpcplanner.ui.components.graph

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PriceChart(
    responseData: List<PVPCModel>,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOURS,
    onMarkerChanged: (Int, Double) -> Unit,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val simpleDateFormat = remember { SimpleDateFormat("h a", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance() }
    val yAxisStep = rememberYAxis(responseData.map { it.pcb.toFloat() })

    LaunchedEffect(responseData) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = responseData.indices.map { it.toFloat() },
                    y = responseData.map { it.pcb.toFloat() })
            }
        }
    }

    CartesianChartHost(
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            marker = rememberDefaultCartesianMarker(
                label = TextComponent(),
                labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,
            ),
            markerVisibilityListener = MarkerVisibilityListener(responseData, onMarkerChanged),
            startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(MaterialTheme.colorScheme.primary),
                guideline = null,
                itemPlacer = VerticalAxis.ItemPlacer.step(step = { yAxisStep.toDouble() }),
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(MaterialTheme.colorScheme.primary),
                guideline = null,
                valueFormatter = getValueFormatter(calendar, timeFormat, simpleDateFormat),
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = { 1 })
            )
        ),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modelProducer = modelProducer,
    )
}

@Composable
private fun rememberYAxis(values: List<Float>) =
    remember(values) {
        if (values.isEmpty()) {
            DefaultYAxisValue
        } else {
            val minPrice = values.minOrNull() ?: DefaultMinPrice
            val maxPrice = values.maxOrNull() ?: DefaultMaxPrice

            val range = maxPrice - minPrice
            if (range == DefaultMinPrice) {
                DefaultYAxisValue
            } else {
                val desiredLabelCount = 5 // Adjust as needed
                val calculatedStep = range / (desiredLabelCount - 1)
                // Optional: Round to a nicer number, e.g., nearest 0.01 or 0.05
                // This is a simple example; you might want more sophisticated rounding
                (calculatedStep * 100).roundToInt() /
                        100f // Round to 2 decimal places
                            .coerceAtLeast(0.01f) // Ensure step is not too small
            }
        }
    }

@Composable
private fun getValueFormatter(
    calendar: Calendar,
    timeFormat: TimeFormat,
    simpleDateFormat: SimpleDateFormat
) = if (timeFormat == TimeFormat.TWENTY_FOUR_HOURS) {
    CartesianValueFormatter.Default
} else {
    CartesianValueFormatter { _, value, _ ->
        calendar.set(Calendar.HOUR_OF_DAY, value.toInt())
        simpleDateFormat.format(calendar.time)
    }
}

private const val DefaultYAxisValue = 0.1f
private const val DefaultMinPrice = 0f
private const val DefaultMaxPrice = 1f
