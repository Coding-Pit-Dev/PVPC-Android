package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import java.time.LocalDate
import javax.inject.Inject

class GetPrices @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(date: String = ""): Result<List<PVPCModel>> =
        repository.getPrices(date.takeIf { it.isNotEmpty() } ?: getDate())

    private fun getDate(): String{
        val date = LocalDate.now()
        return DATE_FORMAT.format(date.year, date.monthValue, date.dayOfMonth)
    }
}

private const val DATE_FORMAT = "%04d-%02d-%02d"