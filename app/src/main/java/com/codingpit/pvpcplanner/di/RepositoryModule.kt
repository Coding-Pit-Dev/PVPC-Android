package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.data.DeviceRepository
import com.codingpit.pvpcplanner.data.DeviceRepositoryImpl
import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.data.PriceRepositoryImpl
import com.codingpit.pvpcplanner.data.SettingsRepository
import com.codingpit.pvpcplanner.data.SettingsRepositoryImpl
import com.codingpit.pvpcplanner.data.local.store.DefaultSettingsStore
import com.codingpit.pvpcplanner.data.local.store.SettingsStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPriceRepository(priceRepositoryImpl: PriceRepositoryImpl): PriceRepository

    @Binds
    abstract fun bindDeviceRepository(deviceRepositoryImpl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    abstract fun bindSettingRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindDataStoreRepository(defaultSettingsStore: DefaultSettingsStore): SettingsStore
}
