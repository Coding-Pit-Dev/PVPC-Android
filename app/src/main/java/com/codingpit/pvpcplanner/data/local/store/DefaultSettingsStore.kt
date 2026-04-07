package com.codingpit.pvpcplanner.data.local.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.codingpit.pvpcplanner.PvpcSettings
import com.codingpit.pvpcplanner.data.mappers.toDomain
import com.codingpit.pvpcplanner.data.mappers.toProto
import com.codingpit.pvpcplanner.domain.models.DarkMode
import com.codingpit.pvpcplanner.domain.models.Settings
import com.codingpit.pvpcplanner.domain.models.TimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSettingsStore
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : SettingsStore {
        override val settings =
            context.settingsDataStore.data.map {
                with(it) {
                    Settings(
                        darkMode = darkMode.toDomain(),
                        timeFormat = timeFormat.toDomain(),
                        yAxisSlots = yAxisSlots,
                        priceThreshold = priceThresholdMilliEurPerKwh.toPriceThresholdEurosPerKwh(),
                    )
                }
            }

        override suspend fun updateDarkMode(darkMode: DarkMode) {
            context.settingsDataStore.updateData { data ->
                data.toBuilder().setDarkMode(darkMode.toProto()).build()
            }
        }

        override suspend fun updateTimeFormat(timeFormat: TimeFormat) {
            context.settingsDataStore.updateData { data ->
                data.toBuilder().setTimeFormat(timeFormat.toProto()).build()
            }
        }

        override suspend fun updatePriceThreshold(threshold: Float) {
            context.settingsDataStore.updateData { data ->
                data
                    .toBuilder()
                    .setPriceThresholdMilliEurPerKwh(threshold.toMilliEurosPerKwh())
                    .build()
            }
        }
    }

private val Context.settingsDataStore: DataStore<PvpcSettings> by dataStore(
    fileName = "settings.pb",
    serializer = PvpcSettingsSerializer,
)
