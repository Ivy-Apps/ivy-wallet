package com.ivy.wallet.domain.data

import com.ivy.wallet.R
import com.ivy.wallet.stringRes
import java.time.LocalDateTime

enum class IntervalType {
    DAY, WEEK, MONTH, YEAR;

    fun forDisplay(intervalN: Int): String {
        val plural = intervalN > 1 || intervalN == 0
        return when (this) {
            DAY -> if (plural) stringRes(R.string.days) else stringRes(R.string.day)
            WEEK -> if (plural) stringRes(R.string.weeks) else stringRes(R.string.week)
            MONTH -> if (plural) stringRes(R.string.months) else stringRes(R.string.month)
            YEAR -> if (plural) stringRes(R.string.years) else stringRes(R.string.year)
        }
    }

    fun incrementDate(date: LocalDateTime, intervalN: Long): LocalDateTime {
        return when (this) {
            DAY -> date.plusDays(intervalN)
            WEEK -> date.plusWeeks(intervalN)
            MONTH -> date.plusMonths(intervalN)
            YEAR -> date.plusYears(intervalN)
        }
    }
}