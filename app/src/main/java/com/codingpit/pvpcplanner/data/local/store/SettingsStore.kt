package com.codingpit.pvpcplanner.data.local.store

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import kotlinx.coroutines.flow.Flow

interface SettingsStore {
    val settings: Flow<Settings>

    suspend fun updateDarkMode(darkMode: DarkMode)

    suspend fun updateTimeFormat(timeFormat: TimeFormat)

    suspend fun updatePriceThreshold(threshold: Float)
}
