package com.codingpit.pvpcplanner.data

import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>

    suspend fun updateDarkMode(darkMode: DarkMode)

    suspend fun updateTimeFormat(timeFormat: TimeFormat)

    suspend fun updatePriceThreshold(threshold: Float)
}
