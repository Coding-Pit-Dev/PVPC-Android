package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun PriceCardComponent(
    widthSize: Int = 200,
    heightSize: Int = 50,
    iconCard: ImageVector = Icons.Outlined.Menu,
    hour: String = "00:00",
    price: String = "0.0",
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .size(width = widthSize.dp, height = heightSize.dp)
    ) {

        Row {

            Icon(
                imageVector = iconCard,
                contentDescription = "Icon Card",
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            Text(
                text = hour,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = price.toString(),
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center,
            )

        }

    }
}

@Preview
@Composable
private fun ContentView_Preview() {
    PriceCardComponent()
}