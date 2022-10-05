package com.ivy.common.time

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

// region Day
// .atStartOfDay() is already built-in in LocalDate

fun LocalDate.atEndOfDay(): LocalDateTime =
    this.atTime(23, 59, 59)
// endregion

// region Week
fun startOfWeek(date: LocalDate): LocalDate =
    date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun endOfWeek(date: LocalDate): LocalDate =
    date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
// endregion

// region Month
fun startOfMonth(date: LocalDate): LocalDate =
    date.withDayOfMonth(1)

fun endOfMonth(date: LocalDate): LocalDate =
    date.withDayOfMonth(date.lengthOfMonth())

fun LocalDate.withDayOfMonthSafe(targetDayOfMonth: Int): LocalDate {
    val maxDayOfMonth = this.lengthOfMonth()
    return this.withDayOfMonth(
        if (targetDayOfMonth > maxDayOfMonth) maxDayOfMonth else targetDayOfMonth
    )
}
// endregion

// region Year
fun startOfYear(date: LocalDate): LocalDate =
    LocalDate.of(date.year, 1, 1)

fun endOfYear(date: LocalDate): LocalDate =
    LocalDate.of(date.year, 12, 31)
// endregion
