package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.SettingsRepository
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import javax.inject.Inject

class UpdateSetting @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(darkMode: DarkMode) {
        settingsRepository.updateDarkMode(darkMode)
    }

    suspend operator fun invoke(timeFormat: TimeFormat) {
        settingsRepository.updateTimeFormat(timeFormat)
    }
}
