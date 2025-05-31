package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.Repository
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class GetPricesFlow @Inject constructor(private val repository: Repository) {
    operator fun invoke(date: String = ""): Flow<Result<List<PVPCModel>>> = flow {
        emit(repository.getPrices(date.takeIf { it.isNotEmpty() } ?: getDate()))
    }

    private fun getDate(): String{
        val date = LocalDate.now()
        return "${date.year}-${date.monthValue}-${date.dayOfMonth}"
    }
}