package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun PricesListComponent() {

    val prices = listOf(10,20,30,40,50,60,70,80,90,100)
    val currentTimeMillis = System.currentTimeMillis()
    val date = Date(currentTimeMillis)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val formattedDate = format.format(date)


    LazyColumn {
        items(prices){
            Text(it.toString())
            PriceCardComponent(
                widthSize = 250,
                heightSize = 50,
                hour = formattedDate,
                price = "${it.toDouble()} €"
            )
        }
    }
}

@Preview
@Composable
private fun PricesView_Preview() {
    PricesListComponent()
}



