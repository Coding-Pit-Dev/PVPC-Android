package com.codingpit.pvpcplanner.domain.usecase

import com.codingpit.pvpcplanner.data.PriceRepository
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.utils.DateChecker
import com.codingpit.pvpcplanner.utils.toParsedDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPricesFlow @Inject constructor(
    private val repository: PriceRepository,
    private val dateChecker: DateChecker,
) {
    operator fun invoke(date: String = ""): Flow<Result<List<PVPCModel>>> =
        flow {
            emit(
                repository.getPrices(
                    date.takeIf { it.isNotEmpty() } ?: dateChecker
                        .getDefaultDate()
                        .toParsedDate(),
                ),
            )
        }
}
