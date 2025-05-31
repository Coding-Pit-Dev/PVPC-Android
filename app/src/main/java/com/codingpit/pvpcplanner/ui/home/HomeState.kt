package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.models.PVPCModel

sealed class HomeState {
    object Loading : HomeState()

    data class Success(
        val pvpcEntries: List<PVPCModel> = emptyList(),
        val currentDate: String = "",
        val nextDateEnabled: Boolean = true
    ) : HomeState()

    data class Error(
        val error: String,
    ) : HomeState()
}
