package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import com.codingpit.pvpcplanner.domains.models.PVPCModel


@Composable
fun PricesListComponent(
    responseData: List<PVPCModel>
) {
    LazyColumn(
    ) {
        items(responseData){ data ->
            PriceCardComponent(
                widthSize = 250,
                heightSize = 70,
                date = data.dia,
                hour = data.hora,
                price =  "${data.pcb}€"
            )
        }
    }
}

@Preview
@Composable
private fun PricesView_Preview() {
   PricesListComponent(responseData = emptyList())
}
