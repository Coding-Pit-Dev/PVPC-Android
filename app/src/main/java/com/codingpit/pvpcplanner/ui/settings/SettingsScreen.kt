package com.codingpit.pvpcplanner.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codingpit.pvpcplanner.R
import com.codingpit.pvpcplanner.ui.components.AnimatedBottomSheet

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = state) {
            is SettingsState.Loading -> SettingsScreen_Loading()
            is SettingsState.Success ->
                SettingsScreen_Success(
                    state = state,
                    onSettingUpdate = { setting, option ->
                        viewModel.updateSetting(setting, option)
                    },
                    onThresholdUpdate = { threshold ->
                        viewModel.updatePriceThreshold(threshold)
                    },
                )

            is SettingsState.Error -> SettingsScreen_Error()
        }
    }
}

@Composable
private fun SettingsScreen_Success(
    state: SettingsState.Success,
    onSettingUpdate: (SettingRender, SettingOption) -> Unit,
    onThresholdUpdate: (Float) -> Unit,
) {
    var selectedSetting by remember {
        mutableStateOf<SettingRender?>(null)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(state.settings) {
            SettingsItem(
                title = stringResource(it.setting.titleRes),
                subTitle = stringResource(it.options.first { option -> option.selected }.labelRes),
                onClick = {
                    selectedSetting = it
                },
            )
        }
        item {
            PriceThresholdItem(
                currentThreshold = state.priceThreshold,
                onThresholdUpdate = onThresholdUpdate,
            )
        }
    }

    SettingsOptionsModal(
        selectedSetting,
        onOptionClick = { setting, option ->
            onSettingUpdate(setting, option)
            selectedSetting = null
        },
        onDismissRequest = {
            selectedSetting = null
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsOptionsModal(
    setting: SettingRender?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onOptionClick: (SettingRender, SettingOption) -> Unit,
) {
    AnimatedBottomSheet(
        modifier = modifier,
        value = setting,
        onDismissRequest = onDismissRequest,
    ) {
        SheetContent(setting?.options ?: emptyList()) {
            setting?.let { p1 -> onOptionClick(p1, it) }
        }
    }
}

@Composable
private fun SheetContent(
    options: List<SettingOption>,
    modifier: Modifier = Modifier,
    onClick: (SettingOption) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(options) { option ->
            SettingsOptionItem(option = option, onClick = onClick)
        }
    }
}

@Composable
private fun SettingsOptionItem(
    option: SettingOption,
    modifier: Modifier = Modifier,
    onClick: (SettingOption) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onClick(option)
                },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = stringResource(option.labelRes))

        if (option.selected) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        }
    }
}

@Composable
private fun PriceThresholdItem(
    currentThreshold: Float,
    modifier: Modifier = Modifier,
    onThresholdUpdate: (Float) -> Unit,
) {
    val initialValue = if (currentThreshold > 0f) currentThreshold.toString() else ""
    var textValue by rememberSaveable { mutableStateOf(initialValue) }
    val thresholdRegex = remember { Regex("^\\d*\\.?\\d*$") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = stringResource(R.string.setting_price_threshold))
        Text(text = stringResource(R.string.setting_price_threshold_description))
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(thresholdRegex)) {
                    textValue = newValue
                    val parsed = newValue.toFloatOrNull() ?: 0f
                    onThresholdUpdate(parsed)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("€/kWh") },
        )
    }
}

@Composable
private fun SettingsScreen_Error() {
}

@Composable
private fun SettingsScreen_Loading() {
}

@Composable
private fun SettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(title)
            subTitle?.let {
                Text(it)
            }
        }

        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
    }
}
