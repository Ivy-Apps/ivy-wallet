package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.base.*
import com.ivy.wallet.ui.theme.modal.model.Month
import java.time.LocalDate
import java.time.LocalDateTime

data class TimePeriod(
    val month: Month? = null,
    val fromToRange: FromToTimeRange? = null,
    val lastNRange: LastNTimeRange? = null,
) {
    companion object {
        /**
         * Examples:
         * 1. startDateOfMonth = 1, today = Nov. 10
         * return Nov. 1 - Nov. 30
         *
         * 2. startDateOfMonth = 10, today = Nov. 9
         * return Oct. 10 - Nov. 9
         *
         * 3. startDateOfMonth = 10, today = Nov. 10
         * return Nov. 10 - Dec. 9
         */
        fun currentMonth(startDayOfMonth: Int): TimePeriod {
            val dateNowUTC = dateNowUTC()
            val dayToday = dateNowUTC.dayOfMonth

            //Examples Nov (7) = Nov (7) till Dec (6)
            //=> new period starts if today => startDayOfMonth
            val newPeriodStarted = dayToday >= startDayOfMonth

            val periodDate = if (newPeriodStarted) {
                dateNowUTC
            } else {
                dateNowUTC.minusMonths(1)
            }

            return TimePeriod(
                month = Month.fromMonthValue(
                    periodDate.monthValue
                )
            )
        }
    }

    fun isValid(): Boolean =
        month != null || fromToRange != null || lastNRange != null

    fun toRange(
        startDateOfMonth: Int
    ): FromToTimeRange {
        return when {
            month != null -> {
                val date = month.toDate()
                val (from, to) = if (startDateOfMonth != 1) {
                    fromToMonthlyRangeForCustomStartDate(date, startDateOfMonth)
                } else {
                    Pair(startOfMonth(date), endOfMonth(date))
                }

                FromToTimeRange(
                    from = from,
                    to = to
                )
            }
            fromToRange != null -> {
                fromToRange
            }
            lastNRange != null -> {
                FromToTimeRange(
                    from = lastNRange.fromDate(),
                    to = timeNowUTC()
                )
            }
            else -> {
                val date = dateNowUTC()
                FromToTimeRange(
                    from = startOfMonth(date),
                    to = endOfMonth(date)
                )
            }
        }
    }

    private fun fromToMonthlyRangeForCustomStartDate(
        date: LocalDate,
        startDateOfMonth: Int
    ): Pair<LocalDateTime, LocalDateTime> {
        val from = date
            .withDayOfMonthSafe(startDateOfMonth)
            .atStartOfDay()

        val to = date
            .withDayOfMonthSafe(startDateOfMonth)
            .plusMonths(1)
            .minusDays(1) //e.g. correct: 14.10-13.11
            .atEndOfDay()

        return Pair(from, to)
    }

    fun toDisplayShort(
        startDateOfMonth: Int
    ): String {
        return when {
            month != null -> {
                if (startDateOfMonth == 1) {
                    month.name
                } else {
                    val range = toRange(startDateOfMonth)
                    "${month.name} (${range.from?.dayOfMonth}-${range.to?.dayOfMonth})"
                }
            }
            fromToRange != null -> {
                fromToRange.toDisplay()
            }
            lastNRange != null -> {
                "Last ${lastNRange.forDisplay()}"
            }
            else -> "Custom"
        }
    }

    fun toDisplayLong(
        startDateOfMonth: Int
    ): String {
        return when {
            month != null -> {
                if (startDateOfMonth == 1) {
                    month.name
                } else {
                    toRange(startDateOfMonth).toDisplay()
                }
            }
            fromToRange != null -> {
                fromToRange.toDisplay()
            }
            lastNRange != null -> {
                "the last ${lastNRange.forDisplay()}"
            }
            else -> {
                toRange(startDateOfMonth).toDisplay()
            }
        }

    }
}