package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codingpit.pvpcplanner.R


@Composable
fun PriceCardComponent(
    date : String,
    hour: String,
    price: Double,
    modifier: Modifier = Modifier,
    iconCard: ImageVector = Icons.Outlined.Menu,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = getBackground(price),
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = iconCard,
                contentDescription = "Icon Card",
                modifier = Modifier.padding(8.dp)
            )
            Column {
                Row {
                    Text(
                        text = date,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = hour,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                Text(
                    text = stringResource(R.string.price_format, price),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun getBackground(price: Double): Color{
   return when{
        price < 0.10 -> Color.Green
        price < 0.15 -> Color.Yellow
        else -> Color.Red
    }
}

@Preview
@Composable
private fun ContentView_Preview() {
    PriceCardComponent(
        date = "01/05/2025",
        hour = "03-04",
        price = 0.087,
    )
}

