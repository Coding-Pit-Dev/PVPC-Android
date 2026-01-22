package com.codingpit.pvpcplanner.ui.home

import com.codingpit.pvpcplanner.domain.usecase.GetPrices
import com.codingpit.pvpcplanner.domain.usecase.GetSettings
import com.codingpit.pvpcplanner.domain.usecase.date.GetDefaultDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalDate
import com.codingpit.pvpcplanner.domain.usecase.date.GetLocalHour
import com.codingpit.pvpcplanner.domain.usecase.date.IsValidDate
import javax.inject.Inject

class HomeUseCaseProvider @Inject constructor(
    val getPrices: GetPrices,
    val getDefaultDate: GetDefaultDate,
    val isValidDate: IsValidDate,
    val getLocalHour: GetLocalHour,
    val getLocalDate: GetLocalDate,
    val getSettings: GetSettings
)
