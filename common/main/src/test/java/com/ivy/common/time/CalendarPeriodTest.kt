package com.ivy.common.time

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localDate
import io.kotest.property.checkAll
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarPeriodTest : StringSpec({
    // region Day
    // the start of the day is already built-in so no need to test it

    "finds the end of the day" {
        val date = LocalDate.of(2022, 5, 10)

        val res = date.atEndOfDay()

        res shouldBe LocalDateTime.of(
            2022, 5, 10,
            23, 59, 59
        )
    }
    // endregion

    // region Week
    "finds the start of the week" {
        // Week 40 of 2022
        val monday = LocalDate.of(2022, 10, 3)
        val sunday = LocalDate.of(2022, 10, 9)

        checkAll(Arb.localDate(monday, sunday)) { week40day ->
            val res = startOfWeek(week40day)

            res shouldBe monday
        }
    }

    "finds the end of the week" {
        // Week 40 of 2022
        val monday = LocalDate.of(2022, 10, 3)
        val sunday = LocalDate.of(2022, 10, 9)

        checkAll(Arb.localDate(monday, sunday)) { week40day ->
            val res = endOfWeek(week40day)

            res shouldBe sunday
        }
    }
    // endregion

    // region Month
    "finds the start of the month" {
        checkAll(
            Arb.localDate(
                minDate = LocalDate.of(2022, 10, 1),
                maxDate = LocalDate.of(2022, 10, 31),
            )
        ) { dateInOctober ->
            val res = startOfMonth(dateInOctober)

            res shouldBe LocalDate.of(2022, 10, 1)
        }
    }

    "finds the end of February" {
        checkAll(
            Arb.localDate(
                minDate = LocalDate.of(2022, 2, 1),
                maxDate = LocalDate.of(2022, 2, 28)
            )
        ) { date ->
            val res = endOfMonth(date)

            res shouldBe LocalDate.of(2022, 2, 28)
        }
    }

    "finds the end of February, leap year" {
        checkAll(
            Arb.localDate(
                minDate = LocalDate.of(2024, 2, 1),
                maxDate = LocalDate.of(2024, 2, 29)
            )
        ) { date ->
            val res = endOfMonth(date)

            res shouldBe LocalDate.of(2024, 2, 29)
        }
    }

    "finds the end of a 31 days month" {
        checkAll(
            Arb.localDate(
                minDate = LocalDate.of(2024, 10, 1),
                maxDate = LocalDate.of(2024, 10, 31)
            )
        ) { date ->
            val res = endOfMonth(date)

            res shouldBe LocalDate.of(2024, 10, 31)
        }
    }

    "finds the end of a 30 days month" {
        checkAll(
            Arb.localDate(
                minDate = LocalDate.of(2024, 11, 1),
                maxDate = LocalDate.of(2024, 11, 30)
            )
        ) { date ->
            val res = endOfMonth(date)

            res shouldBe LocalDate.of(2024, 11, 30)
        }
    }
    // endregion

    // region Year
    "finds the start of the year" {
        checkAll(Arb.localDate()) { date ->
            val res = startOfYear(date)

            res shouldBe LocalDate.of(date.year, 1, 1)
        }
    }

    "finds the end of the year" {
        checkAll(Arb.localDate()) { date ->
            val res = endOfYear(date)

            res shouldBe LocalDate.of(date.year, 12, 31)
        }
    }
    // endregion
})