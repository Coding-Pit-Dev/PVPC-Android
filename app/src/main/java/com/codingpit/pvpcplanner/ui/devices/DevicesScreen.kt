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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.domain.models.DeviceConsumptionInput
import com.codingpit.pvpcplanner.domain.usecase.CalculateTotalConsumption
import com.codingpit.pvpcplanner.utils.getIcons

@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel,
    onDeviceClick: (Device) -> Unit,
) {
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
                    onSwiped = { viewModel.removeDevice(it.device) },
                    onDeviceClick = onDeviceClick,
                    onSearchQueryChanged = viewModel::updateSearchQuery,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesScreen_Success(
    state: DevicesState.Success,
    addDevice: (String, Int, String) -> Unit,
    onSwiped: (DeviceRender) -> Unit,
    onDeviceClick: (Device) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    val calculateTotalConsumption = remember { CalculateTotalConsumption() }
    val summary =
        calculateTotalConsumption(
            state.devicesSlot.map {
                DeviceConsumptionInput(
                    watts = it.device.watts,
                    hours = it.device.hours,
                    cost = it.cost,
                )
            },
        )
    val filteredDevices = state.filteredDevices

    Column(Modifier.fillMaxSize()) {
        if (state.devicesSlot.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(span = { GridItemSpan(1) }) {
                    SearchBar(
                        searchQuery = state.searchQuery,
                        onSearchQueryChanged = onSearchQueryChanged,
                    )
                }

                item(span = { GridItemSpan(1) }) {
                    SummarySection(totalKWh = summary.totalKWh, totalCost = summary.totalCost)
                }

                item(span = { GridItemSpan(1) }) {
                    SectionHeader(deviceCount = filteredDevices.size)
                }

                if (filteredDevices.isEmpty()) {
                    item(span = { GridItemSpan(1) }) {
                        NoResultsState()
                    }
                } else {
                    items(filteredDevices) {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            DeviceItem(
                                render = it,
                                onSwiped = { onSwiped(it) },
                                onClick = { onDeviceClick(it.device) },
                            )
                        }
                    }
                }
            }
        } else {
            EmptyState()
        }
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
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search_device_placeholder),
                fontSize = 15.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.action_close),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                },
            ),
    )
}

@Composable
private fun NoResultsState(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.no_devices_found),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun SummarySection(
    totalKWh: Double,
    totalCost: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.consumption_summary),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )

        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "%.2f kWh".format(totalKWh),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                    Text(
                        text = stringResource(R.string.consumed),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .size(width = 1.dp, height = 40.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "€%.2f".format(totalCost),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(R.string.estimated_cost),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    deviceCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.appliances_section),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        Text(
            text = pluralStringResource(R.plurals.device_count, deviceCount, deviceCount),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DeviceItem(
    render: DeviceRender,
    modifier: Modifier = Modifier,
    onSwiped: (DeviceRender) -> Unit,
    onClick: () -> Unit,
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
            val alignment =
                when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    SwipeToDismissBoxValue.Settled -> Alignment.Center
                }
            val icon =
                when (direction) {
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
                        RoundedCornerShape(16.dp),
                    ).padding(horizontal = 24.dp),
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
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            onClick = onClick,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                val icon = getIcons()[render.device.icon]

                Box(
                    modifier =
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(12.dp),
                            ).size(52.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    } ?: run {
                        Text(
                            text =
                                render.device.name
                                    .firstOrNull()
                                    ?.uppercaseChar()
                                    ?.toString()
                                    ?: "?",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = render.device.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text =
                                stringResource(
                                    R.string.best_time_slot,
                                    render.bestSlot.startHour,
                                    render.bestSlot.endHour,
                                ),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = "${render.device.watts}W",
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "€",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = "~€%.2f".format(render.cost),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
