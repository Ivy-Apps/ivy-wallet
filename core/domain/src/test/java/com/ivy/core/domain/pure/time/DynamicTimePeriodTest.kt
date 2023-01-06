package com.ivy.core.domain.pure.time

import com.ivy.common.test.testTimeProvider
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.time.DynamicTimePeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.next
import java.time.LocalDate
import java.time.LocalDateTime

class DynamicTimePeriodTest : StringSpec({
    fun timeProvider(date: LocalDate): TimeProvider = testTimeProvider(
        date.atTime(Arb.localTime().next())
    )

    // region Convert Calendar period
    "converts daily dynamic period to range" {
        val offset = Arb.int(-10, 10).next()
        val dynamic = DynamicTimePeriod.Calendar(unit = TimeUnit.Day, offset = offset)
        val timeProvider = timeProvider(LocalDate.of(2022, 10, 5))

        val res = dynamic.toRange(startDayOfMonth = 1, timeProvider = timeProvider)

        res shouldBe TimeRange(
            from = LocalDateTime.of(
                2022, 10, 5,
                0, 0, 0
            ).plusDays(offset.toLong()),
            to = LocalDateTime.of(
                2022, 10, 5,
                23, 59, 59
            ).plusDays(offset.toLong())
        )
    }

    "converts weekly dynamic period to range" {
        val offset = Arb.int(-10, 10).next()
        val dynamic = DynamicTimePeriod.Calendar(unit = TimeUnit.Week, offset = offset)
        val timeProvider = timeProvider(LocalDate.of(2022, 10, 5))

        val res = dynamic.toRange(startDayOfMonth = 1, timeProvider = timeProvider)

        res shouldBe TimeRange(
            from = LocalDateTime.of(
                2022, 10, 3,
                0, 0, 0
            ).plusWeeks(offset.toLong()),
            to = LocalDateTime.of(
                2022, 10, 9,
                23, 59, 59
            ).plusWeeks(offset.toLong())
        )
    }

    "converts monthly dynamic period, start day of month 1" {
        val dynamic = DynamicTimePeriod.Calendar(unit = TimeUnit.Month, offset = 0)
        val timeProvider = timeProvider(LocalDate.of(2022, 10, 5))

        val res = dynamic.toRange(startDayOfMonth = 1, timeProvider = timeProvider)

        res shouldBe TimeRange(
            from = LocalDateTime.of(
                2022, 10, 1,
                0, 0, 0
            ),
            to = LocalDateTime.of(
                2022, 10, 31,
                23, 59, 59
            )
        )
    }

    "converts yearly dynamic period to range" {
        val offset = Arb.int(-10, 10).next()
        val dynamic = DynamicTimePeriod.Calendar(unit = TimeUnit.Year, offset = offset)
        val timeProvider = timeProvider(LocalDate.of(2022, 10, 5))

        val res = dynamic.toRange(startDayOfMonth = 1, timeProvider = timeProvider)

        res shouldBe TimeRange(
            from = LocalDateTime.of(
                2022, 1, 1,
                0, 0, 0
            ).plusYears(offset.toLong()),
            to = LocalDateTime.of(
                2022, 12, 31,
                23, 59, 59
            ).plusYears(offset.toLong())
        )
    }
    // endregion

    // Convert "Last" period
    "converts 'last 3 days' period to range" {
        val dynamic = DynamicTimePeriod.Last(n = 3, unit = TimeUnit.Day)
        val timeProvider = timeProvider(LocalDate.of(2022, 10, 5))

        val res = dynamic.toRange(startDayOfMonth = 1, timeProvider = timeProvider)

        res shouldBe TimeRange(
            from = LocalDateTime.of(
                2022, 10, 3,
                0, 0, 0
            ),
            to = LocalDateTime.of(
                2022, 10, 5,
                23, 59, 59
            )
        )
    }
    // endregion

    // TODO: Cover all cases with tests
})