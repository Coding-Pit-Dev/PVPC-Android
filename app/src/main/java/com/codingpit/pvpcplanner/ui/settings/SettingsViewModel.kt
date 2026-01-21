package com.codingpit.pvpcplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import com.codingpit.pvpcplanner.domain.error.handleErrors
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.UpdateSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettings: GetSettings,
    errorHandler: ErrorHandler,
    private val updateSetting: UpdateSetting,
    coroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    val state =
        getSettings().map {
            SettingsState.Success(it.toRender(it))
        }.handleErrors(errorHandler, "settings_data", SettingsState.Factory)
            .flowOn(coroutineDispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState.Loading)


    fun updateSetting(render: SettingRender, option: SettingOption) {
        viewModelScope.launch {
            when (render.setting) {
                is SettingValue.DarkMode -> {

                    updateSetting(
                        when (option.title) {
                            "Light" -> DarkMode.LIGHT
                            "Dark" -> DarkMode.DARK
                            else -> DarkMode.SYSTEM
                        }
                    )
                }

                is SettingValue.TimeFormat -> {
                    updateSetting(
                        when (option.title) {
                            "24 hours" -> TimeFormat.TWENTY_FOUR_HOURS
                            else -> TimeFormat.TWELVE_HOURS
                        }
                    )
                }
            }
        }
    }
}

sealed class SealedSettings(
    val title: String,
    val options: List<SettingOption>,
    val selectedOption: SettingOption
) {
    class DarkMode(
        title: String,
        options: List<SettingOption>,
        selectedOption: SettingOption
    ) : SealedSettings(title, options, selectedOption)

    class TimeFormat(
        title: String,
        options: List<SettingOption>,
        selectedOption: SettingOption
    ) : SealedSettings(title, options, selectedOption)
}

sealed class SettingValue(
    val title: String, val value: String
) {
    class DarkMode(title: String, value: String) : SettingValue(title, value)
    class TimeFormat(title: String, value: String) : SettingValue(title, value)
}


data class SettingRender(
    val setting: SettingValue,
    val options: List<SettingOption>,
)

data class SettingOption(
    val title: String,
    val selected: Boolean = false
)

private fun Settings.toRender(settings: Settings): List<SettingRender> {
    return listOf(
        SettingRender(
            setting = SettingValue.DarkMode(
                "Dark mode",
                getSelectedOption(settings.darkMode).title
            ),
            options = listOf(
                SettingOption("System", settings.darkMode == DarkMode.SYSTEM),
                SettingOption("Light", settings.darkMode == DarkMode.LIGHT),
                SettingOption("Dark", settings.darkMode == DarkMode.DARK),
            ),
        ),
        SettingRender(
            setting = SettingValue.TimeFormat(
                "Time format",
                getSelectedOption(settings.timeFormat).title
            ),
            options = listOf(
                SettingOption("24 hours", settings.timeFormat == TimeFormat.TWENTY_FOUR_HOURS),
                SettingOption("AM/PM", settings.timeFormat == TimeFormat.TWELVE_HOURS),
            ),
        )
    )
}

private fun getSelectedOption(darkMode: DarkMode): SettingOption {
    return when (darkMode) {
        DarkMode.LIGHT -> SettingOption("Light")
        DarkMode.DARK -> SettingOption("Dark")
        DarkMode.SYSTEM -> SettingOption("System")
    }
}

private fun getSelectedOption(timeFormat: TimeFormat): SettingOption {
    return when (timeFormat) {
        TimeFormat.TWELVE_HOURS -> SettingOption("AM/PM")
        TimeFormat.TWENTY_FOUR_HOURS -> SettingOption("24 hours")
    }
}


