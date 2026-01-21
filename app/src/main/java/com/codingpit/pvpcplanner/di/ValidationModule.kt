package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.domain.error.DefaultErrorMessageProvider
import com.codingpit.pvpcplanner.domain.error.ErrorMessageProvider
import com.codingpit.pvpcplanner.domain.validation.DateValidator
import com.codingpit.pvpcplanner.domain.validation.DefaultDateValidator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ValidationModule {

    @Binds
    @Singleton
    abstract fun bindDateValidator(
        defaultDateValidator: DefaultDateValidator
    ): DateValidator

    @Binds
    @Singleton
    abstract fun bindErrorMessageProvider(
        defaultErrorMessageProvider: DefaultErrorMessageProvider
    ): ErrorMessageProvider
}
