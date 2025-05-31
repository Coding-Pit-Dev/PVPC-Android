package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.Device

@Composable
fun DevicesScreen(devicesViewModel: DevicesViewModel) {

    val state by devicesViewModel.state.collectAsStateWithLifecycle()

    Screen {
        when (val state = state) {
            is DevicesState.Error -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(state.error, modifier = Modifier.align(Alignment.Center))
                }
            }
            DevicesState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is DevicesState.Success -> DevicesScreen_Success(
                state,
                { name, hours -> devicesViewModel.addDevice(name, hours) })
        }

    }
}

@Composable
private fun DevicesScreen_Success(state: DevicesState.Success, addDevice: (String, Int) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        var name by remember { mutableStateOf("") }
        var hours by remember { mutableStateOf("") }
        TextField(name, onValueChange = { name = it })
        TextField(
            hours.toString(),
            onValueChange = { hours = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = { addDevice(name, hours.toInt()) }) {
            Text("Add")
        }

        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
           items(state.devicesSlot){
               DeviceItem(it)
           }
        }
    }


}

@Composable
private fun DeviceItem(device: DeviceRender, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(device.device.name)
            Text(device.device.hours.toString())

            Text(
                stringResource(
                    R.string.best_time_from_to,
                    device.bestSlot.startHour,
                    device.bestSlot.endHour
                )
            )
        }
    }
}

@Composable
fun Screen(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(modifier = modifier.systemBarsPadding(), content = content)
}