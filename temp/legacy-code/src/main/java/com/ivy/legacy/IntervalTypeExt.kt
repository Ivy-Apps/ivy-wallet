package com.ivy.legacy

import com.ivy.base.legacy.stringRes
import com.ivy.data.model.IntervalType
import com.ivy.ui.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

fun IntervalType.forDisplay(intervalN: Int): String {
    val plural = intervalN > 1 || intervalN == 0
    return when (this) {
        IntervalType.DAY -> if (plural) stringRes(R.string.days) else stringRes(R.string.day)
        IntervalType.WEEK -> if (plural) stringRes(R.string.weeks) else stringRes(R.string.week)
        IntervalType.MONTH -> if (plural) stringRes(R.string.months) else stringRes(R.string.month)
        IntervalType.YEAR -> if (plural) stringRes(R.string.years) else stringRes(R.string.year)
    }
}

@Suppress("MagicNumber")
fun IntervalType.incrementDate(date: Instant, intervalN: Long): Instant {
    return when (this) {
        IntervalType.DAY -> date.plus(intervalN, ChronoUnit.DAYS)
        IntervalType.WEEK -> date.plus(intervalN * 7, ChronoUnit.DAYS)
        IntervalType.MONTH -> date.atZone(ZoneOffset.UTC).plusMonths(intervalN).toInstant()
        IntervalType.YEAR -> date.atZone(ZoneOffset.UTC).plusYears(intervalN).toInstant()
    }
}
