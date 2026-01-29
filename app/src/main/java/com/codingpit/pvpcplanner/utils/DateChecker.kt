package com.codingpit.pvpcplanner.utils

import java.time.LocalDate
import javax.inject.Inject

class DateChecker
@Inject
constructor() {
    /**
     * Gets the default date for price queries.
     * Delegates to DateFormatter for consistent behavior.
     */
    fun getDefaultDate(): LocalDate = DateFormatter.getDefaultQueryDate()

    /**
     * Validates if a date is available for querying.
     * Delegates to DateFormatter for consistent behavior.
     */
    fun checkValidDate(date: LocalDate): Boolean = DateFormatter.isValidQueryDate(date)
}
