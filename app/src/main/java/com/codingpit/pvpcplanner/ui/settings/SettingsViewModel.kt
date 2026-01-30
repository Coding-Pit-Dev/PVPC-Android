package com.codingpit.pvpcplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingpit.pvpcplanner.R
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
class SettingsViewModel
    @Inject
    constructor(
        getSettings: GetSettings,
        errorHandler: ErrorHandler,
        private val updateSetting: UpdateSetting,
        private val coroutineDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        val state =
            getSettings()
                .map {
                    SettingsState.Success(it.toRender())
                }.handleErrors(errorHandler, "settings_data", SettingsState.Factory)
                .flowOn(coroutineDispatcher)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState.Loading)

        fun updateSetting(
            render: SettingRender,
            option: SettingOption,
        ) {
            viewModelScope.launch(coroutineDispatcher) {
                when (render.setting) {
                    is SettingValue.DarkMode -> {
                        updateSetting(
                            when (option.labelRes) {
                                R.string.option_light -> DarkMode.LIGHT
                                R.string.option_dark -> DarkMode.DARK
                                else -> DarkMode.SYSTEM
                            },
                        )
                    }

                    is SettingValue.TimeFormat -> {
                        updateSetting(
                            when (option.labelRes) {
                                R.string.option_24h -> TimeFormat.TWENTY_FOUR_HOURS
                                else -> TimeFormat.TWELVE_HOURS
                            },
                        )
                    }
                }
            }
        }
    }

sealed class SettingValue(
    val titleRes: Int,
    val valueRes: Int,
) {
    class DarkMode(
        titleRes: Int,
        valueRes: Int,
    ) : SettingValue(titleRes, valueRes)

    class TimeFormat(
        titleRes: Int,
        valueRes: Int,
    ) : SettingValue(titleRes, valueRes)
}

data class SettingRender(
    val setting: SettingValue,
    val options: List<SettingOption>,
)

data class SettingOption(
    val labelRes: Int,
    val selected: Boolean = false,
)

private fun Settings.toRender(): List<SettingRender> =
    listOf(
        SettingRender(
            setting =
                SettingValue.DarkMode(
                    R.string.setting_dark_mode,
                    getSelectedOption(darkMode).labelRes,
                ),
            options =
                listOf(
                    SettingOption(R.string.option_system, darkMode == DarkMode.SYSTEM),
                    SettingOption(R.string.option_light, darkMode == DarkMode.LIGHT),
                    SettingOption(R.string.option_dark, darkMode == DarkMode.DARK),
                ),
        ),
        SettingRender(
            setting =
                SettingValue.TimeFormat(
                    R.string.setting_time_format,
                    getSelectedOption(timeFormat).labelRes,
                ),
            options =
                listOf(
                    SettingOption(
                        R.string.option_24h,
                        timeFormat == TimeFormat.TWENTY_FOUR_HOURS,
                    ),
                    SettingOption(
                        R.string.option_ampm,
                        timeFormat == TimeFormat.TWELVE_HOURS,
                    ),
                ),
        ),
    )

private fun getSelectedOption(darkMode: DarkMode): SettingOption =
    when (darkMode) {
        DarkMode.LIGHT -> SettingOption(R.string.option_light)
        DarkMode.DARK -> SettingOption(R.string.option_dark)
        DarkMode.SYSTEM -> SettingOption(R.string.option_system)
    }

private fun getSelectedOption(timeFormat: TimeFormat): SettingOption =
    when (timeFormat) {
        TimeFormat.TWELVE_HOURS -> SettingOption(R.string.option_ampm)
        TimeFormat.TWENTY_FOUR_HOURS -> SettingOption(R.string.option_24h)
    }
