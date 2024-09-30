package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domains.models.PVPCModel

sealed class HomeState {
    object Loading : HomeState()

    data class Success(
        val data: List<PVPCModel>,
    ) : HomeState()

    data class Error(
        val error: String,
    ) : HomeState()
}
