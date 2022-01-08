package com.ivy.wallet.ui.onboarding.model

import android.util.Log
import com.ivy.wallet.base.*
import com.ivy.wallet.ui.theme.modal.model.Month
import java.time.LocalDate
import java.time.LocalDateTime

data class TimePeriod(
    val month: Month? = null,
    val year: Int? = null,
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

            //Examples month = Nov. startDate = 7; Period = from Nov (7) till Dec (6)
            // => new period starts if today => startDayOfMonth
            val newPeriodStarted = dayToday >= startDayOfMonth

            val periodDate = if (newPeriodStarted) {
                //new monthly period has already started then observe it => current month
                dateNowUTC
            } else {
                //new monthly period hasn't yet started then observe the ongoing one => previous month
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
                val date = if (year!=null) month.toDate().withYear(year) else month.toDate()
                val (from, to) = if (startDateOfMonth != 1) {
                    customStartDayOfMonthPeriodRange(
                        date = date,
                        startDateOfMonth = startDateOfMonth
                    )
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

    private fun customStartDayOfMonthPeriodRange(
        date: LocalDate,
        startDateOfMonth: Int
    ): Pair<LocalDateTime, LocalDateTime> {
        val from = date
            .withDayOfMonthSafe(startDateOfMonth)
            .atStartOfDay()

        val to = date
            .withDayOfMonthSafe(startDateOfMonth)
            //startDayOfMonth != 1 just shift N day the month forward so to should +1 month
            .plusMonths(1)
            //e.g. Correct: 14.10-13.11 (Incorrect: 14.10-14.11)
            .minusDays(1)
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
                    val pattern = "MMM dd"
                    //Don't use formatLocal() because .to is at 23:59:59 =>
                    // it may appear as +1 day in some timeZones when converted
                    "${range.from?.format(pattern)} - ${range.to?.format(pattern)}"
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