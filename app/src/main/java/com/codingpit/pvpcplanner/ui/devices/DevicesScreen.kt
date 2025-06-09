package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.ui.components.AnimatedBottomSheet

@Composable
fun DevicesScreen(devicesViewModel: DevicesViewModel, modifier: Modifier = Modifier) {

    val state by devicesViewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = modifier) {
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
                state = state,
                addDevice = { name, hours -> devicesViewModel.addDevice(name, hours) },
                hideAddDeviceModal = { devicesViewModel.hideAddDeviceModal() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesScreen_Success(
    state: DevicesState.Success,
    addDevice: (String, Int) -> Unit,
    hideAddDeviceModal: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
            items(state.devicesSlot) {
                DeviceItem(it)
            }
        }

        AddDeviceModal(
            isVisible = state.showModal,
            onDismissRequest = hideAddDeviceModal,
            addDevice = addDevice
        )
    }
}

@Composable
private fun DeviceItem(
    device: DeviceRender,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.EndToStart -> Color.Green
                    SwipeToDismissBoxValue.StartToEnd -> Color.Red
                }
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.Settled -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Done
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                SwipeToDismissBoxValue.Settled -> Icons.Default.AccountBox
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Localized description",
                    modifier = Modifier.scale(scale)
                )
            }
        },
    ) {
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDeviceModal(
    modifier: Modifier = Modifier,
    isVisible: Boolean?,
    onDismissRequest: () -> Unit,
    addDevice: (String, Int) -> Unit
) {
    AnimatedBottomSheet(
        modifier = modifier,
        value = isVisible,
        onDismissRequest = onDismissRequest
    ) {
        SheetContent(addDevice)
    }
}

@Composable
private fun SheetContent(addDevice: (String, Int) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth())
        TextField(
            value = hours.toString(),
            onValueChange = { hours = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { addDevice(name, hours.toInt()) }) {
            Text("Add")
        }
    }


}


