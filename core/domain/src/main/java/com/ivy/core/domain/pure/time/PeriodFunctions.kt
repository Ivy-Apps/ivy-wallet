package com.ivy.core.domain.pure.time

import com.ivy.common.atEndOfDay
import com.ivy.common.beginningOfIvyTime
import com.ivy.common.endOfIvyTime
import com.ivy.common.toEpochSeconds
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit
import java.time.LocalDate
import java.time.LocalDateTime

fun allTime(): TimeRange = TimeRange(
    from = beginningOfIvyTime(),
    to = endOfIvyTime()
)

fun shiftTime(time: LocalDateTime, n: Int, unit: TimeUnit): LocalDateTime {
    val nLong = n.toLong()
    return when (unit) {
        TimeUnit.Day -> time.plusDays(nLong)
        TimeUnit.Week -> time.plusWeeks(nLong)
        TimeUnit.Month -> time.plusMonths(nLong)
        TimeUnit.Year -> time.plusYears(nLong)
    }
}

fun periodLengthDays(range: TimeRange): Int {
    val secondsDiff = range.to.toEpochSeconds() - range.from.toEpochSeconds()
    val daysLong = java.util.concurrent.TimeUnit.SECONDS.toDays(secondsDiff)
    return daysLong.toInt()
}

fun yearPeriod(year: Int): SelectedPeriod.CustomRange = SelectedPeriod.CustomRange(
    range = TimeRange(
        from = LocalDate.of(year, 1, 1).atStartOfDay(),
        to = LocalDate.of(year, 12, 31).atEndOfDay()
    )
)
