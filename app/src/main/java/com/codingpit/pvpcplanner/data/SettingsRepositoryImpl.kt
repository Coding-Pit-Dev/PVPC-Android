package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.data.local.store.SettingsStore
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl
    @Inject
    constructor(
        private val settingsStore: SettingsStore,
    ) : SettingsRepository {
        override fun getSettings(): Flow<Settings> = settingsStore.settings

        override suspend fun updateDarkMode(darkMode: DarkMode) {
            settingsStore.updateDarkMode(darkMode)
        }

        override suspend fun updateTimeFormat(timeFormat: TimeFormat) {
            settingsStore.updateTimeFormat(timeFormat)
        }

        override suspend fun updatePriceThreshold(threshold: Float) {
            require(threshold >= 0f) { "priceThreshold must be >= 0" }
            settingsStore.updatePriceThreshold(threshold)
        }
    }
