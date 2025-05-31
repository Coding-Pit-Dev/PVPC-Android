package com.codingpit.pvpcplanner.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class DateChecker @Inject constructor() {
    val localDate: LocalDate = LocalDate.now()
    val localHour: Int

    init {
        val spainZoneId = ZoneId.of(DEFAULT_TIME_ZONE)
        val spainTime = LocalDateTime.now(spainZoneId)
        localHour = spainTime.hour
    }

    fun getDefaultDate(): LocalDate {
        return when {
            localHour >= DEFAULT_REFRESH_DATA_HOUR -> localDate.plusDays(1)
            else -> localDate
        }
    }

    fun checkValidDate(date: LocalDate): Boolean {
        val currentDate = LocalDate.now()
        val spainZoneId = ZoneId.of(DEFAULT_TIME_ZONE)
        val spainTime = LocalDateTime.now(spainZoneId)
        val localHour = spainTime.hour

        return when {
            date.isBefore(currentDate) -> true
            isSameDay(date, currentDate) && localHour >= DEFAULT_REFRESH_DATA_HOUR -> true
            else -> false
        }
    }

    private fun isSameDay(date1: LocalDate, date2: LocalDate): Boolean =
        date1.dayOfMonth == date2.dayOfMonth &&
                date1.monthValue == date2.monthValue &&
                date1.year == date2.year


}

fun LocalDate.toParsedDate(): String = DATE_FORMAT.format(year, monthValue, dayOfMonth)


private const val DATE_FORMAT = "%04d-%02d-%02d"
private const val DEFAULT_REFRESH_DATA_HOUR = 21
private const val DEFAULT_TIME_ZONE = "Europe/Madrid"