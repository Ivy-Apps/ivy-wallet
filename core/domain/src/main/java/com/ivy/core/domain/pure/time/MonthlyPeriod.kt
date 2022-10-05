package com.ivy.core.domain.pure.time

import com.ivy.common.time.*
import com.ivy.data.time.Month
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange
import java.time.LocalDate

// TODO: Refactor this file cuz it's bad...

// TODO: Fix edge-cases when re-working time
fun currentMonthlyPeriod(
    startDayOfMonth: Int
): SelectedPeriod {
    val dateNowUTC = dateNowUTC()
    val dayToday = dateNowUTC.dayOfMonth

    //Examples month = Nov. startDate = 7; Period = from Nov (7) till Dec (6)
    // => new period starts if today => startDayOfMonth
    val newPeriodStarted = dayToday >= startDayOfMonth

    val periodDate = if (newPeriodStarted) {
        //new monthly period has already started then observe it => current month
        dateNowUTC
    } else {
        //new monthly period hasn't yet started then observe the ongoing one => previous month
        dateNowUTC.minusMonths(1)
    }

    return dateToSelectedMonthlyPeriod(
        dateInPeriod = periodDate,
        startDayOfMonth = startDayOfMonth,
    )
}

fun dateToSelectedMonthlyPeriod(
    dateInPeriod: LocalDate,
    startDayOfMonth: Int,
): SelectedPeriod.Monthly = SelectedPeriod.Monthly(
    month = Month(
        number = dateInPeriod.monthValue,
        year = dateInPeriod.year,
    ),
    startDayOfMonth = startDayOfMonth,
    range = dateToMonthlyPeriod(date = dateInPeriod, startDayOfMonth = startDayOfMonth),
)

private fun dateToMonthlyPeriod(date: LocalDate, startDayOfMonth: Int): TimeRange =
    if (startDayOfMonth != 1) {
        customStartDayOfMonthPeriodRange(
            date = date,
            startDateOfMonth = startDayOfMonth
        )
    } else {
        TimeRange(
            from = startOfMonth(date).atStartOfDay(),
            to = endOfMonth(date).atEndOfDay()
        )
    }

private fun customStartDayOfMonthPeriodRange(
    date: LocalDate,
    startDateOfMonth: Int
): TimeRange {
    val from = date
        .withDayOfMonthSafe(startDateOfMonth)
        .atStartOfDay()

    val to = date
        //startDayOfMonth != 1 just shift N day the month forward so to should +1 month
        .plusMonths(1)
        .withDayOfMonthSafe(startDateOfMonth)
        //e.g. Correct: 14.10-13.11 (Incorrect: 14.10-14.11)
        .minusDays(1)
        .atEndOfDay()

    return TimeRange(from = from, to = to)
}