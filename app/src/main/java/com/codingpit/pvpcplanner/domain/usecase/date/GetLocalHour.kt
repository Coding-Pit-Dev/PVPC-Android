package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.utils.DateFormatter
import javax.inject.Inject

class GetLocalHour
    @Inject
    constructor() {
        operator fun invoke() = DateFormatter.getCurrentHour()
    }
