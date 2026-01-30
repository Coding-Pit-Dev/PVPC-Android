package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.error.ErrorResult
import com.codingpit.pvpcplanner.domain.error.ErrorState
import com.codingpit.pvpcplanner.domain.error.HasErrorState
import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.codingpit.pvpcplanner.domain.models.TimeFormat

sealed class HomeState {
    object Loading : HomeState()

    data class Success(
        val pvpcEntries: List<PVPCModel> = emptyList(),
        val selectedDate: String = "",
        val currentDate: String = "",
        val nextDateEnabled: Boolean = true,
        val currentPrice: Double = 0.0,
        val currentHour: Int = 0,
        val timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOURS,
    ) : HomeState()

    data class Error(
        override val error: String,
    ) : HomeState(),
        ErrorState

    companion object Factory : HasErrorState<HomeState> {
        override fun createErrorState(errorResult: ErrorResult): HomeState = Error(errorResult.message)
    }
}
