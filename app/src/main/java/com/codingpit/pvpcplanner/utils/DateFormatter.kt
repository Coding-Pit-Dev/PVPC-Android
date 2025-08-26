package com.codingpit.pvpcplanner.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Centralized date formatting and utility class.
 * Handles all date-related operations consistently across the application.
 */
object DateFormatter {

    /**
     * Default date format pattern used throughout the application.
     * Format: YYYY-MM-DD (ISO 8601 standard)
     */
    const val DEFAULT_DATE_FORMAT = "%04d-%02d-%02d"

    /**
     * Default time zone for the application (Spain).
     */
    const val DEFAULT_TIME_ZONE = "Europe/Madrid"

    /**
     * Hour when new price data becomes available (21:00 Spanish time).
     */
    const val DEFAULT_REFRESH_DATA_HOUR = 21

    /**
     * Formats a LocalDate to string using the standard application format.
     *
     * @param date The LocalDate to format
     * @return Formatted date string in YYYY-MM-DD format
     */
    fun formatDate(date: LocalDate): String {
        return DEFAULT_DATE_FORMAT.format(date.year, date.monthValue, date.dayOfMonth)
    }

    /**
     * Formats a LocalDate using year, month, and day components.
     *
     * @param year The year
     * @param month The month (1-12)
     * @param day The day of month
     * @return Formatted date string in YYYY-MM-DD format
     */
    fun formatDate(year: Int, month: Int, day: Int): String {
        return DEFAULT_DATE_FORMAT.format(year, month, day)
    }

    /**
     * Gets the current date in the application's time zone.
     *
     * @return Current LocalDate in Spain time zone
     */
    fun getCurrentDate(): LocalDate {
        return LocalDate.now(ZoneId.of(DEFAULT_TIME_ZONE))
    }

    /**
     * Gets the current date and time in the application's time zone.
     *
     * @return Current LocalDateTime in Spain time zone
     */
    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now(ZoneId.of(DEFAULT_TIME_ZONE))
    }

    /**
     * Gets the current hour in the application's time zone.
     *
     * @return Current hour (0-23) in Spain time zone
     */
    fun getCurrentHour(): Int {
        return getCurrentDateTime().hour
    }

    /**
     * Checks if two dates represent the same day.
     *
     * @param date1 First date to compare
     * @param date2 Second date to compare
     * @return true if both dates are on the same day
     */
    fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean {
        return date1.dayOfMonth == date2.dayOfMonth &&
                date1.monthValue == date2.monthValue &&
                date1.year == date2.year
    }

    /**
     * Determines the default date for price queries based on current time.
     * If it's past the data refresh hour, returns tomorrow's date.
     *
     * @return The appropriate date for price queries
     */
    fun getDefaultQueryDate(): LocalDate {
        val currentDate = getCurrentDate()
        val currentHour = getCurrentHour()

        return when {
            currentHour >= DEFAULT_REFRESH_DATA_HOUR -> currentDate.plusDays(1)
            else -> currentDate
        }
    }

    /**
     * Validates if a date is available for querying based on business rules.
     * Data is available for past dates and current date after refresh hour.
     *
     * @param date The date to validate
     * @return true if the date is valid for querying
     */
    fun isValidQueryDate(date: LocalDate): Boolean {
        val currentDate = getCurrentDate()
        val currentHour = getCurrentHour()

        return when {
            date.isBefore(currentDate) -> true
            isSameDay(date, currentDate) && currentHour >= DEFAULT_REFRESH_DATA_HOUR -> true
            else -> false
        }
    }
}

/**
 * Extension function to convert LocalDate to the standard application date string format.
 */
fun LocalDate.toParsedDate(): String = DateFormatter.formatDate(this)
