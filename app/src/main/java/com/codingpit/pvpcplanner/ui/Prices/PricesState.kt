package com.codingpit.pvpcplanner.ui.Prices

import com.codingpit.pvpcplanner.domains.models.PVPCModel

sealed class PricesState {
    object Idle : PricesState()

    data class Success(
        val data: PVPCModel?,
    ) : PricesState()

    data class Error(
        val error: String,
    ) : PricesState()
}