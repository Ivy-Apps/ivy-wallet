package com.ivy.common.time

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localDate
import io.kotest.property.checkAll
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarPeriodTest : StringSpec({
    "at the end of the day" {
        val date = LocalDate.of(2022, 5, 10)

        val res = date.atEndOfDay()

        res shouldBe LocalDateTime.of(
            2022, 5, 10,
            23, 59, 59
        )
    }

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
})