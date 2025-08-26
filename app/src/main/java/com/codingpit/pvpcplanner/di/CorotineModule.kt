package com.codingpit.pvpcplanner.di

import android.content.Context
import androidx.room.Room
import com.codingpit.pvpcplanner.data.local.db.PVPCDao
import com.codingpit.pvpcplanner.data.local.db.PVPCDatabase
import com.codingpit.pvpcplanner.data.local.sources.DefaultLocalDataSource
import com.codingpit.pvpcplanner.data.local.sources.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CorotineModule {
    @Singleton
    @Provides
    fun providesCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
