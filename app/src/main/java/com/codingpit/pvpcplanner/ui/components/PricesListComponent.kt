package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.codingpit.pvpcplanner.domains.models.PVPCDTO
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.LineHeightStyle
import java.lang.reflect.Modifier


@Composable
fun PricesListComponent(
    responseData: List<PVPCDTO>
) {
    LazyColumn(
    ) {
        items(responseData){ data ->
            PriceCardComponent(
                widthSize = 250,
                heightSize = 70,
                date = data.dia.toString(),
                hour = data.hora.toString(),
                price =  "${data.pcb.toString()}€"
            )
        }
    }
}

@Preview
@Composable
private fun PricesView_Preview() {
   PricesListComponent(responseData = emptyList())
}
