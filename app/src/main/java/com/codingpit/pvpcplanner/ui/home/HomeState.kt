package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.models.PVPCModel

sealed class HomeState {
    object Loading : HomeState()

    data class Success(
        val pvpcEntries: List<PVPCModel> = emptyList(),
        val selectedDate: String = "",
        val currentDate: String = "",
        val nextDateEnabled: Boolean = true,
        val currentPrice: Double = 0.0,
        val currentHour: Int = 0
    ) : HomeState()

    data class Error(
        val error: String,
    ) : HomeState()
}
