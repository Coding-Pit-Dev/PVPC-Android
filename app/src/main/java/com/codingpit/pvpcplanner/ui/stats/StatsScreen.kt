package com.codingpit.pvpcplanner.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val s = state) {
            is StatsState.Loading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is StatsState.Success -> StatsScreen_Success(s.summaries)

            is StatsState.Error ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(s.error)
                }
        }
    }
}

@Composable
private fun StatsScreen_Success(summaries: List<DailyPriceSummary>) {
    if (summaries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.stats_no_data))
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.stats_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        items(summaries) { summary ->
            DailySummaryCard(summary)
        }
    }
}

@Composable
private fun DailySummaryCard(
    summary: DailyPriceSummary,
    modifier: Modifier = Modifier,
) {
    Card(shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(summary.day, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.stats_avg),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(R.string.price_format, summary.averagePrice),
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.stats_min),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(R.string.price_format, summary.minPrice),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.stats_max),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(R.string.price_format, summary.maxPrice),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
