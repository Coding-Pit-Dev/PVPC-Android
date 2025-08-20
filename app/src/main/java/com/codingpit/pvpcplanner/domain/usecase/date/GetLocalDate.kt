package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.utils.DateFormatter
import javax.inject.Inject

class GetLocalDate
    @Inject
    constructor() {
        operator fun invoke() = DateFormatter.getCurrentDate()
    }
