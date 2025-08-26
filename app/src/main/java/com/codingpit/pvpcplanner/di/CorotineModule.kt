package com.codingpit.pvpcplanner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
