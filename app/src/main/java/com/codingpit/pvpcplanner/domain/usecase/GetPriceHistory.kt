package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary
import javax.inject.Inject

class GetPriceHistory
    @Inject
    constructor(
        private val repository: PriceRepository,
    ) {
        suspend operator fun invoke(): Result<List<DailyPriceSummary>> = repository.getPriceHistory()
    }
