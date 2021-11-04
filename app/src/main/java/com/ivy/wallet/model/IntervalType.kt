package com.ivy.wallet.model

import java.time.LocalDateTime

enum class IntervalType {
    DAY, WEEK, MONTH, YEAR;

    fun forDisplay(intervalN: Int): String {
        val plural = intervalN > 1 || intervalN == 0
        return when (this) {
            DAY -> if (plural) "days" else "day"
            WEEK -> if (plural) "weeks" else "week"
            MONTH -> if (plural) "months" else "month"
            YEAR -> if (plural) "years" else "year"
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