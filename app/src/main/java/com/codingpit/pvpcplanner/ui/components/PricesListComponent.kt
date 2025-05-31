package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codingpit.pvpcplanner.domain.models.PVPCModel


@Composable
fun PricesListComponent(
    responseData: List<PVPCModel>
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(responseData){ data ->
            PriceCardComponent(
                date = data.day,
                hour = "From ${data.startHour} to ${data.endHour}",
                price = data.pcb
            )
        }
    }
}

@Preview
@Composable
private fun PricesView_Preview() {
   PricesListComponent(responseData = emptyList())
}
