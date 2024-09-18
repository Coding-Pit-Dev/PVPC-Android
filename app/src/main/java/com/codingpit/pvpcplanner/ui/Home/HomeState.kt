package com.codingpit.pvpcplanner.ui.Home

import com.codingpit.pvpcplanner.domains.models.PVPCDTO

sealed class HomeState {
    object Loading : HomeState()

    data class Success(
        val data: List<PVPCDTO>,
    ) : HomeState()

    data class Error(
        val error: String,
    ) : HomeState()
}
