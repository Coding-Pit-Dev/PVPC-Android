package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codingpit.pvpcplanner.R


@Composable
fun PriceCardComponent(
    hour: String,
    price: Double,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = hour,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.price_format, price),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier
                .size(20.dp)
                .background(getBackground(price), CircleShape))
        }
    }
}

private fun getBackground(price: Double): Color {
    return when {
        price < 0.10 -> Color.Green
        price < 0.15 -> Color.Yellow
        else -> Color.Red
    }
}

@Preview
@Composable
private fun ContentView_Preview() {
    PriceCardComponent(
        hour = "03 - 04",
        price = 0.087,
    )
}

