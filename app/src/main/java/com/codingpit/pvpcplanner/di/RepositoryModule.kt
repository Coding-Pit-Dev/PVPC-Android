package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.data.RepositoryImpl
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
    abstract fun bindRepository(repositoryImpl: RepositoryImpl): Repository

    @Binds
    abstract fun bindSettingRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindDataStoreRepository(defaultSettingsStore: DefaultSettingsStore): SettingsStore
}
