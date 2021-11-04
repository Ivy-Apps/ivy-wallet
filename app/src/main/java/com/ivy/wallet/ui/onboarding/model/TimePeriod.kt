package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.base.*
import com.ivy.wallet.ui.theme.modal.model.Month

data class TimePeriod(
    val month: Month? = null,
    val fromToRange: FromToTimeRange? = null,
    val lastNRange: LastNTimeRange? = null,
) {
    companion object {
        fun thisMonth(): TimePeriod =
            TimePeriod(
                month = Month.fromMonthValue(dateNowUTC().monthValue)
            )
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
                    val from = date
                        .withDayOfMonthSafe(startDateOfMonth)
                        .atStartOfDay()

                    val to = date.plusMonths(1)
                        .withDayOfMonthSafe(startDateOfMonth)
                        .minusDays(1) //e.g. correct: 14.10-13.11
                        .atEndOfDay()

                    Pair(from, to)
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