package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.domain.error.DefaultErrorHandler
import com.codingpit.pvpcplanner.domain.error.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {

    @Binds
    @Singleton
    abstract fun bindErrorHandler(
        defaultErrorHandler: DefaultErrorHandler
    ): ErrorHandler
}