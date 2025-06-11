package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.utils.DateChecker
import java.time.LocalDate
import javax.inject.Inject


class IsValidDate @Inject constructor(private val dateChecker: DateChecker) {
    operator fun invoke(date: LocalDate) =
        dateChecker.checkValidDate(date)
}