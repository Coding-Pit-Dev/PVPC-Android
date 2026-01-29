package com.codingpit.pvpcplanner.ui.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.domain.models.Device
import com.codingpit.pvpcplanner.utils.DeviceCategory
import com.codingpit.pvpcplanner.utils.DeviceIcon
import com.codingpit.pvpcplanner.utils.getDeviceCategories
import com.codingpit.pvpcplanner.utils.getDeviceIcons

@Composable
fun DeviceAddScreen(
    viewModel: DeviceAddViewModel,
    device: Device? = null,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val icons = getDeviceIcons()
    val categories = getDeviceCategories()

    LaunchedEffect(device) {
        if (device != null) {
            viewModel.initializeForEdit(device, icons, categories)
        } else {
            viewModel.initialize(icons, categories)
        }
    }

    when (val currentState = state) {
        is DeviceAddState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DeviceAddState.Success -> {
            DeviceAddScreen_Success(
                state = currentState,
                onBackClick = onBackClick,
                onDeviceNameChanged = viewModel::onDeviceNameChanged,
                onWattsChanged = viewModel::onWattsChanged,
                onHoursChanged = viewModel::onHoursChanged,
                onAlwaysOnChanged = viewModel::onAlwaysOnChanged,
                onNotesChanged = viewModel::onNotesChanged,
                onIconSelected = viewModel::onIconSelected,
                onCategorySelected = viewModel::onCategorySelected,
                onShowIconPicker = viewModel::showIconPicker,
                onHideIconPicker = viewModel::hideIconPicker,
                onShowCategoryPicker = viewModel::showCategoryPicker,
                onHideCategoryPicker = viewModel::hideCategoryPicker,
                onSaveClick = { viewModel.saveDevice(onBackClick) },
            )
        }

        is DeviceAddState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(currentState.error)
                    Button(onClick = onBackClick) {
                        Text(stringResource(R.string.action_back))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceAddScreen_Success(
    state: DeviceAddState.Success,
    onBackClick: () -> Unit,
    onDeviceNameChanged: (String) -> Unit,
    onWattsChanged: (String) -> Unit,
    onHoursChanged: (String) -> Unit,
    onAlwaysOnChanged: (Boolean) -> Unit,
    onNotesChanged: (String) -> Unit,
    onIconSelected: (DeviceIcon) -> Unit,
    onCategorySelected: (DeviceCategory) -> Unit,
    onShowIconPicker: () -> Unit,
    onHideIconPicker: () -> Unit,
    onShowCategoryPicker: () -> Unit,
    onHideCategoryPicker: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
                Text(
                    text =
                        if (state.isEditMode) {
                            stringResource(R.string.edit_device_title)
                        } else {
                            stringResource(R.string.add_device_title)
                        },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            state.selectedIcon?.let { icon ->
                IconSection(
                    selectedIcon = icon,
                    onChangeIconClick = onShowIconPicker,
                )
            }

            NameField(
                value = state.deviceName,
                onValueChange = onDeviceNameChanged,
            )

            AlwaysOnField(
                checked = state.alwaysOn,
                onCheckedChange = onAlwaysOnChanged,
            )

            ConsumptionRow(
                watts = state.watts,
                onWattsChange = onWattsChanged,
                hours = state.hours,
                onHoursChange = onHoursChanged,
                alwaysOn = state.alwaysOn,
                hoursError = state.hoursError,
            )

            state.selectedCategory?.let { category ->
                CategoryField(
                    selectedCategory = category,
                    onShowCategoryPicker = onShowCategoryPicker,
                )
            }

            NotesField(
                value = state.notes,
                onValueChange = onNotesChanged,
            )

            ButtonsSection(
                onSaveClick = onSaveClick,
                onCancelClick = onBackClick,
                saveEnabled = state.saveEnabled,
                isSaving = state.isSaving,
                isEditMode = state.isEditMode,
            )
        }

        if (state.showIconPicker && state.selectedIcon != null) {
            IconPickerModal(
                icons = state.availableIcons,
                selectedIcon = state.selectedIcon!!,
                onIconSelected = onIconSelected,
                onDismiss = onHideIconPicker,
            )
        }

        if (state.showCategoryPicker && state.selectedCategory != null) {
            CategoryPickerModal(
                categories = state.availableCategories,
                selectedCategory = state.selectedCategory!!,
                onCategorySelected = onCategorySelected,
                onDismiss = onHideCategoryPicker,
            )
        }
    }
}

@Composable
private fun IconSection(
    selectedIcon: DeviceIcon,
    onChangeIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(20.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = selectedIcon.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }

        Surface(
            onClick = onChangeIconClick,
            shape = RoundedCornerShape(100.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(R.string.change_icon),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.device_name_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.device_name_placeholder)) },
            shape = RoundedCornerShape(12.dp),
        )
    }
}

@Composable
private fun AlwaysOnField(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.always_on),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = stringResource(R.string.always_on_description),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun ConsumptionRow(
    watts: String,
    onWattsChange: (String) -> Unit,
    hours: String,
    onHoursChange: (String) -> Unit,
    alwaysOn: Boolean,
    hoursError: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.consumption_watts),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            OutlinedTextField(
                value = watts,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onWattsChange(newValue)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                suffix = { Text("W/h") },
            )
        }

        if (!alwaysOn) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.program_duration),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                OutlinedTextField(
                    value = hours,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                            onHoursChange(newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    suffix = { Text(stringResource(R.string.unit_hours)) },
                    isError = hoursError != null,
                    supportingText = if (hoursError != null) {
                        {
                            Text(
                                text = stringResource(
                                    when (hoursError) {
                                        "error_validation_device_hours_max" -> R.string.error_validation_device_hours_max
                                        else -> R.string.error_validation_generic
                                    }
                                ),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else null,
                )
            }
        }
    }
}

@Composable
private fun CategoryField(
    selectedCategory: DeviceCategory,
    onShowCategoryPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.category_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        Surface(
            onClick = onShowCategoryPicker,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border =
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(selectedCategory.labelRes),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun NotesField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.notes_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .size(height = 80.dp, width = 0.dp),
            placeholder = { Text(stringResource(R.string.notes_placeholder)) },
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
        )
    }
}

@Composable
private fun ButtonsSection(
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    saveEnabled: Boolean,
    isSaving: Boolean,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onSaveClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .size(height = 52.dp, width = 0.dp),
            enabled = saveEnabled && !isSaving,
            shape = RoundedCornerShape(12.dp),
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        stringResource(
                            if (isEditMode) R.string.update_device else R.string.save_device,
                        ),
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onCancelClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .size(height = 52.dp, width = 0.dp),
            enabled = !isSaving,
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(R.string.action_cancel))
        }
    }
}

@Composable
private fun IconPickerModal(
    icons: List<DeviceIcon>,
    selectedIcon: DeviceIcon,
    onIconSelected: (DeviceIcon) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.select_icon),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(icons) { icon ->
                        IconPickerItem(
                            icon = icon,
                            isSelected = selectedIcon.id == icon.id,
                            onClick = { onIconSelected(icon) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconPickerItem(
    icon: DeviceIcon,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

    val contentColor =
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon.icon,
                contentDescription = stringResource(icon.labelRes),
                modifier = Modifier.size(28.dp),
                tint = contentColor,
            )
            Text(
                text = stringResource(icon.labelRes),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryPickerModal(
    categories: List<DeviceCategory>,
    selectedCategory: DeviceCategory,
    onCategorySelected: (DeviceCategory) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
        ) {
            Column {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.select_category),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(categories) { category ->
                        CategoryPickerItem(
                            category = category,
                            isSelected = selectedCategory.id == category.id,
                            onClick = { onCategorySelected(category) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPickerItem(
    category: DeviceCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }

    val contentColor =
        if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(category.labelRes),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
