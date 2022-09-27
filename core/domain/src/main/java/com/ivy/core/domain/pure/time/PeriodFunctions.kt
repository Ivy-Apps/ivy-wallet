package com.ivy.core.domain.pure.time

import com.ivy.common.atEndOfDay
import com.ivy.common.beginningOfIvyTime
import com.ivy.common.endOfIvyTime
import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeUnit
import java.time.LocalDate
import java.time.LocalDateTime

fun allTime(): Period.FromTo = Period.FromTo(
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

fun yearPeriod(year: Int): SelectedPeriod.CustomRange = SelectedPeriod.CustomRange(
    period = Period.FromTo(
        from = LocalDate.of(year, 1, 1).atStartOfDay(),
        to = LocalDate.of(year, 12, 31).atEndOfDay()
    )
)
