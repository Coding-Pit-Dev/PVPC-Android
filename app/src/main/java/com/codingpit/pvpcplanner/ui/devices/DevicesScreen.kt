package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.ui.components.AnimatedBottomSheet
import com.codingpit.pvpcplanner.utils.DeviceIcon
import com.codingpit.pvpcplanner.utils.getDeviceIcons
import com.codingpit.pvpcplanner.utils.getIcons

@Composable
fun DevicesScreen(viewModel: DevicesViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
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

            is DevicesState.Success ->
                DevicesScreen_Success(
                    state = state,
                    addDevice = { name, hours, icon -> viewModel.addDevice(name, hours, icon) },
                    hideAddDeviceModal = { viewModel.hideAddDeviceModal() },
                    onSwiped = { viewModel.removeDevice(it.device) },
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesScreen_Success(
    state: DevicesState.Success,
    addDevice: (String, Int, String) -> Unit,
    hideAddDeviceModal: () -> Unit,
    onSwiped: (DeviceRender) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        if (state.devicesSlot.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.devicesSlot) {
                    DeviceItem(it) {
                        onSwiped(it)
                    }
                }
            }
        } else {
            EmptyState()
        }

        AddDeviceModal(
            isVisible = state.showModal,
            onDismissRequest = hideAddDeviceModal,
            addDevice = addDevice,
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.no_devices),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun DeviceItem(
    render: DeviceRender,
    modifier: Modifier = Modifier,
    onSwiped: (DeviceRender) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        onDismiss = {
            onSwiped(render)
        },
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Red
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    SwipeToDismissBoxValue.StartToEnd -> Color.Red
                },
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.Settled -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                SwipeToDismissBoxValue.Settled -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        color,
                        CardDefaults.shape,
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.action_delete),
                    modifier = Modifier.scale(scale),
                )
            }
        },
    ) {
        Card(modifier = modifier) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val icon = getIcons()[render.device.icon]

                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp),
                                )
                                .padding(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondary,
                    )
                } ?: run {
                    Text(
                        render.device.name.first().uppercase(),
                        modifier =
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp),
                                )
                                .padding(16.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                ) {
                    Text(
                        text = "${render.device.name} (${render.device.hours} ${stringResource(R.string.unit_hours)})",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    )

                    Text(
                        text =
                            stringResource(
                                R.string.best_time_from_to,
                                render.bestSlot.startHour,
                                render.bestSlot.endHour,
                            ),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }
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
    addDevice: (String, Int, String) -> Unit,
) {
    AnimatedBottomSheet(
        modifier = modifier,
        value = isVisible,
        onDismissRequest = onDismissRequest,
    ) {
        SheetContent(addDevice)
    }
}

@Composable
private fun SheetContent(
    addDevice: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }

    val buttonEnabled by remember { derivedStateOf { name.isNotEmpty() && hours.isNotEmpty() } }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val icons = getDeviceIcons()
        var selected by remember { mutableStateOf(icons.first()) }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(140.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(icons) { icon ->
                val label = stringResource(icon.labelRes)
                DeviceModalItem(
                    icon = icon,
                    label = label,
                    selected = selected == icon
                ) {
                    selected = icon
                    name = label
                }
            }

            item(span = {
                GridItemSpan(2)
            }) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.label_name))
                        },
                    )
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(stringResource(R.string.label_hours))
                        },
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonEnabled,
                        onClick = { addDevice(name, hours.toInt(), selected.id) },
                    ) {
                        Text(stringResource(R.string.action_add))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceModalItem(
    icon: DeviceIcon,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        border = CardDefaults.outlinedCardBorder(enabled = selected),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon.icon, contentDescription = label)
            Text(label)
        }
    }
}