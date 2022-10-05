package com.ivy.core.domain.pure.time

import com.ivy.common.time.atEndOfDay
import com.ivy.common.time.endOfMonth
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.startOfMonth
import com.ivy.common.time.withDayOfMonthSafe
import com.ivy.data.time.Month
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange
import java.time.LocalDate


fun currentMonthlyPeriod(
    startDayOfMonth: Int,
    timeProvider: TimeProvider
): SelectedPeriod {
    val dateNow = timeProvider.dateNow()

    //Example: today startDate = 7; Period = from Nov (7) till Dec (6)
    // => new period starts if today => startDayOfMonth
    val newPeriodStarted = dateNow.dayOfMonth >= startDayOfMonth

    val periodDate = if (newPeriodStarted) {
        // new monthly period has already started then observe it => current month
        dateNow
    } else {
        // new monthly period hasn't yet started then observe the ongoing one => previous month
        dateNow.minusMonths(1)
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