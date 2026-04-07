package com.codingpit.pvpcplanner.ui.stats

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState
import com.codingpit.pvpcplanner.domain.models.DailyPriceSummary

sealed class StatsState {
    object Loading : StatsState()

    data class Success(
        val summaries: List<DailyPriceSummary> = emptyList(),
    ) : StatsState()

    data class Error(
        override val error: String,
    ) : StatsState(),
        ErrorState

    companion object Factory : HasErrorState<StatsState> {
        override fun createErrorState(errorResult: ErrorResult): StatsState = Error(errorResult.message)
    }
}
