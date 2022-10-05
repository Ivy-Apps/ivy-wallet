package com.ivy.core.domain.pure.time

import com.ivy.common.test.testTimeProvider
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

class DynamicPeriodTest : StringSpec({
    // region Convert Calendar period
    "converts daily dynamic period to range" {
        val offset = Arb.int(-10, 10).next()
        val dynamic = DynamicTimePeriod.Calendar(unit = TimeUnit.Day, offset = offset)
        val timeProvider = testTimeProvider(
            LocalDate.of(2022, 10, 5)
                .atTime(Arb.localTime().next())
        )

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
    // endregion
})