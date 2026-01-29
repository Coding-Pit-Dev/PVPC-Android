package com.codingpit.pvpcplanner.domain.validation

import com.codingpit.pvpcplanner.utils.DateFormatter
import java.time.LocalDate
import javax.inject.Inject

/**
 * Validator interface for date validation operations.
 * Provides a flexible and extensible approach to date validation.
 */
interface DateValidator {
    /**
     * Validates if a date is acceptable for querying price data.
     *
     * @param date The date to validate
     * @return ValidationResult indicating success or failure with details
     */
    fun validateQueryDate(date: LocalDate): ValidationResult

    /**
     * Validates if a date falls within an acceptable range.
     *
     * @param date The date to validate
     * @param startDate The earliest acceptable date (inclusive)
     * @param endDate The latest acceptable date (inclusive)
     * @return ValidationResult indicating success or failure with details
     */
    fun validateDateRange(
        date: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
    ): ValidationResult

    /**
     * Validates if a date is not in the future beyond what's allowed.
     *
     * @param date The date to validate
     * @return ValidationResult indicating success or failure with details
     */
    fun validateNotTooFarInFuture(date: LocalDate): ValidationResult
}

/**
 * Result of a validation operation.
 */
sealed class ValidationResult {
    /**
     * Validation succeeded.
     */
    object Success : ValidationResult()

    /**
     * Validation failed with a specific reason.
     *
     * @param reason Human-readable reason for the failure
     * @param errorCode Optional error code for programmatic handling
     */
    data class Error(
        val reason: String,
        val errorCode: String? = null,
    ) : ValidationResult()
}

/**
 * Default implementation of DateValidator using application business rules.
 */
class DefaultDateValidator @Inject constructor() : DateValidator {
    override fun validateQueryDate(date: LocalDate): ValidationResult {
        val isValid = DateFormatter.isValidQueryDate(date)

        return if (isValid) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(
                reason = "Price data is not yet available for this date. Data becomes available at ${DateFormatter.DEFAULT_REFRESH_DATA_HOUR}:00 Spanish time.",
                errorCode = "PRICE_DATA_NOT_AVAILABLE",
            )
        }
    }

    override fun validateDateRange(
        date: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
    ): ValidationResult =
        when {
            date.isBefore(startDate) ->
                ValidationResult.Error(
                    reason = "Date is before the allowed range. Earliest date: ${
                        DateFormatter.formatDate(
                            startDate,
                        )
                    }",
                    errorCode = "DATE_TOO_EARLY",
                )

            date.isAfter(endDate) ->
                ValidationResult.Error(
                    reason = "Date is after the allowed range. Latest date: ${
                        DateFormatter.formatDate(
                            endDate,
                        )
                    }",
                    errorCode = "DATE_TOO_LATE",
                )

            else -> ValidationResult.Success
        }

    override fun validateNotTooFarInFuture(date: LocalDate): ValidationResult {
        val maxFutureDate = DateFormatter.getCurrentDate().plusDays(MAX_FUTURE_DAYS)

        return if (date.isAfter(maxFutureDate)) {
            ValidationResult.Error(
                reason = "Date is too far in the future. Maximum allowed: ${
                    DateFormatter.formatDate(
                        maxFutureDate,
                    )
                }",
                errorCode = "DATE_TOO_FAR_FUTURE",
            )
        } else {
            ValidationResult.Success
        }
    }

    companion object {
        /**
         * Maximum number of days in the future that are allowed for validation.
         */
        private const val MAX_FUTURE_DAYS = 7L
    }
}
