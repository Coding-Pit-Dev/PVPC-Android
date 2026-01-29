package com.codingpit.pvpcplanner.domain.usecase.date

import com.codingpit.pvpcplanner.domain.validation.DateValidator
import com.codingpit.pvpcplanner.domain.validation.ValidationResult
import java.time.LocalDate
import javax.inject.Inject

class IsValidDate @Inject constructor(
    private val dateValidator: DateValidator,
) {
    /**
     * Validates if a date is acceptable for querying price data.
     *
     * @param date The date to validate
     * @return true if the date is valid, false otherwise
     */
    operator fun invoke(date: LocalDate): Boolean =
        when (dateValidator.validateQueryDate(date)) {
            is ValidationResult.Success -> true
            is ValidationResult.Error -> false
        }
}
