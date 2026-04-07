package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.PriceFetchResult
import com.codingpit.pvpcplanner.utils.DateFormatter
import java.time.LocalDate
import javax.inject.Inject

class GetPrices @Inject constructor(
    private val repository: PriceRepository,
) {
    suspend operator fun invoke(date: String = ""): Result<PriceFetchResult> =
        repository.getPrices(date.takeIf { it.isNotEmpty() } ?: getDate())

    private fun getDate(): String {
        val date = LocalDate.now()
        return DateFormatter.formatDate(date)
    }
}
