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

// region Current monthly period
fun currentMonthlyPeriod(
    startDayOfMonth: Int,
    timeProvider: TimeProvider
): SelectedPeriod {
    val today = timeProvider.dateNow()

    //Example: today = Nov (7), startDate = 7;
    // Current period = from Nov (7) till Dec (6)
    // => new period starts ony if "today => startDayOfMonth"
    val newPeriodStarted = today.dayOfMonth >= startDayOfMonth

    val periodDate = if (newPeriodStarted) {
        // new monthly period has already started then observe it => current month
        today
    } else {
        // new monthly period hasn't yet started then observe the ongoing one => previous month
        today.minusMonths(1)
    }

    return monthlyPeriod(
        dateInPeriod = periodDate,
        startDayOfMonth = startDayOfMonth,
    )
}
// endregion

// region Monthly period from date
fun monthlyPeriod(
    dateInPeriod: LocalDate,
    startDayOfMonth: Int,
): SelectedPeriod.Monthly = SelectedPeriod.Monthly(
    month = Month(
        number = dateInPeriod.monthValue,
        year = dateInPeriod.year,
    ),
    startDayOfMonth = startDayOfMonth,
    range = monthlyTimeRange(date = dateInPeriod, startDayOfMonth = startDayOfMonth),
)

fun monthlyTimeRange(date: LocalDate, startDayOfMonth: Int): TimeRange =
    if (startDayOfMonth != 1) {
        val from = date
            .withDayOfMonthSafe(startDayOfMonth)
            .atStartOfDay()

        val to = date
            .plusMonths(1)
            .withDayOfMonthSafe(startDayOfMonth)
            //e.g. Correct: 14.10-13.11 (Incorrect: 14.10-14.11)
            .minusDays(1)
            .atEndOfDay()

        TimeRange(from = from, to = to)
    } else {
        TimeRange(
            from = startOfMonth(date).atStartOfDay(),
            to = endOfMonth(date).atEndOfDay()
        )
    }
// endregion