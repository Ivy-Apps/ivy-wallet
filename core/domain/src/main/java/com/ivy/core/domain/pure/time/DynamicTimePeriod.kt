package com.ivy.core.domain.pure.time

import com.ivy.common.time.*
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.time.DynamicTimePeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit

fun DynamicTimePeriod.toRange(
    startDayOfMonth: Int,
    timeProvider: TimeProvider
): TimeRange = when (this) {
    is DynamicTimePeriod.Calendar -> toRange(startDayOfMonth, timeProvider)
    is DynamicTimePeriod.Last -> toRange(timeProvider)
    is DynamicTimePeriod.Next -> TODO()
}

// region Calendar
fun DynamicTimePeriod.Calendar.toRange(
    startDayOfMonth: Int,
    timeProvider: TimeProvider,
): TimeRange {
    val today = timeProvider.dateNow()
    val offset = offset.toLong()
    return when (unit) {
        TimeUnit.Day -> today.plusDays(offset)
            .let {
                TimeRange(
                    from = it.atStartOfDay(),
                    to = it.atEndOfDay()
                )
            }
        TimeUnit.Week -> today.plusWeeks(offset).let {
            TimeRange(
                from = startOfWeek(it).atStartOfDay(),
                to = endOfWeek(it).atEndOfDay()
            )
        }
        TimeUnit.Month -> monthlyTimeRange(
            date = today.plusMonths(offset), startDayOfMonth = startDayOfMonth
        )
        TimeUnit.Year -> today.plusYears(offset).let {
            TimeRange(
                from = startOfYear(it).atStartOfDay(),
                to = endOfYear(it).atEndOfDay()
            )
        }
    }
}
// endregion

// region Last
fun DynamicTimePeriod.Last.toRange(
    timeProvider: TimeProvider,
): TimeRange {
    val today = timeProvider.dateNow()
    val adjustedN = n.toLong() - 1 // because it includes today
    return TimeRange(
        from = when (unit) {
            TimeUnit.Day -> today.minusDays(adjustedN)
            TimeUnit.Week -> today.minusWeeks(adjustedN)
            TimeUnit.Month -> today.minusMonths(adjustedN)
            TimeUnit.Year -> today.minusYears(adjustedN)
        }.atStartOfDay(),
        to = today.atEndOfDay()
    )
}
// endregion

// region Next
fun DynamicTimePeriod.Next.toRange(
    timeProvider: TimeProvider,
): TimeRange {
    val today = timeProvider.dateNow()
    val adjustedN = n.toLong() - 1 // because it includes today
    return TimeRange(
        from = today.atStartOfDay(),
        to = when (unit) {
            TimeUnit.Day -> today.plusDays(adjustedN)
            TimeUnit.Week -> today.plusWeeks(adjustedN)
            TimeUnit.Month -> today.plusMonths(adjustedN)
            TimeUnit.Year -> today.plusYears(adjustedN)
        }.atStartOfDay()
    )
}
// endregion