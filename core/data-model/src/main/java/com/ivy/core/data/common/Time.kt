package com.ivy.core.data.common

import java.time.LocalDateTime

enum class WeekDay(val value: Int) {
    Monday(1),
    Tuesday(2),
    Wednesday(3),
    Thursday(4),
    Friday(5),
    Saturday(6),
    Sunday(7);

    companion object {
        fun new(value: Int): WeekDay = when (value) {
            1 -> Monday
            2 -> Tuesday
            3 -> Wednesday
            4 -> Thursday
            5 -> Friday
            6 -> Saturday
            7 -> Sunday
            else -> error("WeekDay error: Invalid week day with number $value.")
        }
    }
}

/**
 * An int between 1 and 31 inclusively representing a date in a month.
 * Use [MonthDate.of] to create one.
 */
@JvmInline
value class MonthDate private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the int isn't between 1 and 31
         * @return a valid [MonthDate]
         */
        fun of(value: Int): MonthDate = if (value in 1..31)
            MonthDate(value) else error("MonthDate error: $value is not a valid date in a month")
    }
}

enum class Month(val value: Int) {
    January(1),
    February(2),
    March(3),
    April(4),
    May(5),
    June(6),
    July(7),
    August(8),
    September(9),
    October(10),
    November(11),
    December(12);

    companion object {
        fun new(value: Int): Month = when (value) {
            1 -> January
            2 -> February
            3 -> March
            4 -> April
            5 -> May
            6 -> June
            7 -> July
            8 -> August
            9 -> September
            10 -> October
            11 -> November
            12 -> December
            else -> error("Month error: Invalid month with number $value.")
        }
    }
}

data class YearDate(
    val month: Month,
    val date: MonthDate,
)

data class TimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime
)

sealed interface TimePeriod {
    data class Fixed(val range: TimeRange) : TimePeriod

    sealed interface Calendar : TimePeriod {
        object Daily : Calendar
        data class Weekly(val startDay: WeekDay) : Calendar
        data class Monthly(val startDate: MonthDate) : Calendar
        data class Yearly(val startMonth: Month, val startDate: MonthDate) : Calendar
    }
}

sealed interface RepeatInterval {
    data class Fixed(
        val intervalSeconds: Long
    ) : RepeatInterval

    sealed interface Calendar : RepeatInterval {
        object Daily : Calendar

        data class Weekly(
            val day: WeekDay
        ) : RepeatInterval

        data class Monthly(
            val date: MonthDate
        ) : RepeatInterval

        data class Yearly(
            val month: Month,
            val date: MonthDate
        ) : RepeatInterval
    }
}