package com.ivy.core.domain.pure.time

import com.ivy.common.time.*
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.time.DynamicTimePeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit

// region Calendar
fun DynamicTimePeriod.toRange(
    startDayOfMonth: Int,
    timeProvider: TimeProvider
): TimeRange = when (this) {
    is DynamicTimePeriod.Calendar -> toRange(startDayOfMonth, timeProvider)
    is DynamicTimePeriod.Last -> TODO()
    is DynamicTimePeriod.Next -> TODO()
}

fun DynamicTimePeriod.Calendar.toRange(
    startDayOfMonth: Int,
    timeProvider: TimeProvider,
): TimeRange {
    val now = timeProvider.dateNow()
    val offset = offset.toLong()
    return when (unit) {
        TimeUnit.Day -> now.plusDays(offset)
            .let {
                TimeRange(
                    from = it.atStartOfDay(),
                    to = it.atEndOfDay()
                )
            }
        TimeUnit.Week -> now.plusWeeks(offset).let {
            TimeRange(
                from = startOfWeek(it).atStartOfDay(),
                to = endOfWeek(it).atEndOfDay()
            )
        }
        TimeUnit.Month -> monthlyTimeRange(
            date = now.plusMonths(offset), startDayOfMonth = startDayOfMonth
        )
        TimeUnit.Year -> now.plusYears(offset).let {
            TimeRange(
                from = startOfYear(it).atStartOfDay(),
                to = endOfYear(it).atEndOfDay()
            )
        }
    }
}
// endregion