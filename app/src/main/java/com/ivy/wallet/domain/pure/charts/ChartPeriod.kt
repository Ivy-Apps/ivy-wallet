package com.ivy.wallet.domain.pure.charts

import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.utils.dateNowUTC
import com.ivy.wallet.utils.endOfDayNowUTC
import com.ivy.wallet.utils.endOfMonth
import com.ivy.wallet.utils.format
import java.time.LocalDateTime

enum class ChartPeriod {
    LAST_12_MONTHS,
    LAST_6_MONTHS,
    LAST_4_WEEKS,
    LAST_7_DAYS;

    fun display(): String {
        return when (this) {
            LAST_12_MONTHS -> "Last 12 months"
            LAST_6_MONTHS -> "Last 6 months"
            LAST_4_WEEKS -> "Last 4 weeks"
            LAST_7_DAYS -> "Last 7 days"
        }
    }

    fun toRangesList(): List<ClosedTimeRange> {
        return when (this) {
            LAST_12_MONTHS -> lastNMonths(n = 12)
            LAST_6_MONTHS -> lastNMonths(n = 6)
            LAST_4_WEEKS -> lastNWeeks(n = 4)
            LAST_7_DAYS -> lastNDays(n = 7)
        }
    }

    private tailrec fun lastNMonths(
        n: Int,
        accumulator: List<ClosedTimeRange> = emptyList(),
        endOfMonth: LocalDateTime = endOfMonth(dateNowUTC())
    ): List<ClosedTimeRange> {
        return if (accumulator.size < n) {
            //recurse
            lastNMonths(
                n = n,
                accumulator = accumulator.plus(
                    ClosedTimeRange(
                        from = endOfMonth.withDayOfMonth(1),
                        to = endOfMonth
                    )
                ),
                endOfMonth = endOfMonth(
                    endOfMonth.withDayOfMonth(10) //not sure if this line is needed
                        .minusMonths(1)
                        .toLocalDate()
                )
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }

    private tailrec fun lastNWeeks(
        n: Int,
        accumulator: List<ClosedTimeRange> = emptyList(),
        endOfDay: LocalDateTime = endOfDayNowUTC()
    ): List<ClosedTimeRange> {
        return if (accumulator.size < n) {
            //recurse
            lastNWeeks(
                n = n,
                accumulator = accumulator.plus(
                    ClosedTimeRange(
                        from = endOfDay.minusDays(7).toLocalDate().atStartOfDay(),
                        to = endOfDay
                    )
                ),
                endOfDay = endOfDay.minusDays(8)
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }

    private fun lastNDays(
        n: Int,
        accumulator: List<ClosedTimeRange> = emptyList(),
        endOfDay: LocalDateTime = endOfDayNowUTC()
    ): List<ClosedTimeRange> {
        return if (accumulator.size < n) {
            //recurse
            lastNDays(
                n = n,
                accumulator = accumulator.plus(
                    ClosedTimeRange(
                        from = endOfDay.toLocalDate().atStartOfDay(),
                        to = endOfDay
                    )
                ),
                endOfDay = endOfDay.minusDays(1)
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }

    fun xLabel(range: ClosedTimeRange): String {
        return when (this) {
            LAST_12_MONTHS -> {
                range.to.monthLetter()
            }
            LAST_6_MONTHS -> {
                range.to.monthLetter()
            }
            LAST_4_WEEKS -> {
                range.to.monthLetter() + range.to.format("dd")
            }
            LAST_7_DAYS -> {
                range.to.monthLetter() + range.to.format("dd")
            }
        }
    }

    private fun LocalDateTime.monthLetter(): String {
        return this.month.name.first().uppercase()
    }
}