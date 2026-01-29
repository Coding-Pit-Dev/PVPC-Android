package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.utils.DateChecker
import javax.inject.Inject

class GetDefaultDate
    @Inject
    constructor(
        private val dateChecker: DateChecker,
    ) {
        operator fun invoke() = dateChecker.getDefaultDate()
    }
