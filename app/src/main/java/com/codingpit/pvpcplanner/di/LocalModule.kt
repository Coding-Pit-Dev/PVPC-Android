package com.codingpit.pvpcplanner.di

import android.content.Context
import androidx.room.Room
import com.codingpit.pvpcplanner.data.local.DefaultLocalDataSource
import com.codingpit.pvpcplanner.data.local.LocalDataSource
import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.local.db.PVPCDatabase
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
    fun providesRoomDatabase(@ApplicationContext applicationContext: Context): PVPCDatabase =
        Room.databaseBuilder(
            applicationContext,
            PVPCDatabase::class.java, "pvpc-database"
        ).build()

    @Provides
    fun providesPvpcDao(database: PVPCDatabase): PVPCDao = database.pvpcDao()

    @Provides
    fun provideLocalDataSource(localDataSource: DefaultLocalDataSource): LocalDataSource {
        return localDataSource
    }
}


