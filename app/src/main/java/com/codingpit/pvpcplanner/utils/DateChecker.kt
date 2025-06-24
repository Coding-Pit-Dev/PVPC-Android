package com.codingpit.pvpcplanner.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class DateChecker
    @Inject
    constructor() {
        internal val localDate: LocalDate = LocalDate.now()
        internal val localHour: Int

        init {
            val spainZoneId = ZoneId.of(DefaultTimeZone)
            val spainTime = LocalDateTime.now(spainZoneId)
            localHour = spainTime.hour
        }

        fun getDefaultDate(): LocalDate {
            return when {
                localHour >= DefaultRefreshDataHour -> localDate.plusDays(1)
                else -> localDate
            }
        }

        fun checkValidDate(date: LocalDate): Boolean {
            val currentDate = LocalDate.now()
            val spainZoneId = ZoneId.of(DefaultTimeZone)
            val spainTime = LocalDateTime.now(spainZoneId)
            val localHour = spainTime.hour

            return when {
                date.isBefore(currentDate) -> true
                isSameDay(date, currentDate) && localHour >= DefaultRefreshDataHour -> true
                else -> false
            }
        }

        private fun isSameDay(
            date1: LocalDate,
            date2: LocalDate,
        ): Boolean =
            date1.dayOfMonth == date2.dayOfMonth &&
                date1.monthValue == date2.monthValue &&
                date1.year == date2.year
    }

fun LocalDate.toParsedDate(): String = DefaultDateFormat.format(year, monthValue, dayOfMonth)

private const val DefaultDateFormat = "%04d-%02d-%02d"
private const val DefaultRefreshDataHour = 21
private const val DefaultTimeZone = "Europe/Madrid"
