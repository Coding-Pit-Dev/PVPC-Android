package com.codingpit.pvpcplanner.di

import com.codingpit.pvpcplanner.domain.strategy.BestTimeSlotCalculationStrategy
import com.codingpit.pvpcplanner.domain.strategy.PriceCalculationStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StrategyModule {
    @Binds
    abstract fun bindPriceCalculationStrategy(bestTimeSlotCalculationStrategy: BestTimeSlotCalculationStrategy): PriceCalculationStrategy
}
