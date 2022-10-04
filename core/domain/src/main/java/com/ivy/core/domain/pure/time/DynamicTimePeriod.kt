package com.ivy.core.domain.pure.time

import com.ivy.data.time.DynamicTimePeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit

// region Calendar
fun DynamicTimePeriod.toRange(startDayOfMonth: Int): TimeRange = when (this) {
    is DynamicTimePeriod.Calendar -> TODO()
    is DynamicTimePeriod.Last -> TODO()
    is DynamicTimePeriod.Next -> TODO()
}

fun DynamicTimePeriod.Calendar.toRange(
    startDayOfMonth: Int
): TimeRange = when (unit) {
    TimeUnit.Day -> TODO()
    TimeUnit.Week -> TODO()
    TimeUnit.Month -> TODO()
    TimeUnit.Year -> TODO()
}
// endregion