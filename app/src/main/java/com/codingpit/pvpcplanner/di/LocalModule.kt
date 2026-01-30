package com.codingpit.pvpcplanner.di

import android.content.Context
import androidx.room.Room
import com.codingpit.pvpcplanner.data.local.db.MIGRATION_1_2
import com.codingpit.pvpcplanner.data.local.db.MIGRATION_2_3
import com.codingpit.pvpcplanner.data.local.db.MIGRATION_3_4
import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.local.db.PVPCDatabase
import com.codingpit.pvpcplanner.data.local.sources.DefaultDeviceLocalDataSource
import com.codingpit.pvpcplanner.data.local.sources.DefaultPriceLocalDataSource
import com.codingpit.pvpcplanner.data.local.sources.DeviceLocalDataSource
import com.codingpit.pvpcplanner.data.local.sources.PriceLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun providesRoomDatabase(
        @ApplicationContext applicationContext: Context,
    ): PVPCDatabase =
        Room
            .databaseBuilder(
                applicationContext,
                PVPCDatabase::class.java,
                "pvpc-database",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

    @Provides
    fun providesPvpcDao(database: PVPCDatabase): PVPCDao = database.pvpcDao()

    @Provides
    fun providePriceLocalDataSource(localDataSource: DefaultPriceLocalDataSource): PriceLocalDataSource = localDataSource

    @Provides
    fun provideDeviceLocalDataSource(localDataSource: DefaultDeviceLocalDataSource): DeviceLocalDataSource = localDataSource
}
