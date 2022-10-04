package com.ivy.common.time

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class TimeConversionTest : StringSpec({
    fun timeProvider(
        timeNow: LocalDateTime = LocalDateTime.now(),
        zoneOffsetHours: Int,
    ) = object : TimeProvider {
        override fun timeNow(): LocalDateTime = timeNow

        override fun dateNow(): LocalDate = timeNow.toLocalDate()

        override fun zoneId(): ZoneId =
            ZoneId.ofOffset("GMT", ZoneOffset.ofHours(zoneOffsetHours))
    }

    "local <> utc" {
        val localTime = LocalDateTime.of(
            2022, 10, 4,
            22, 35, 0
        )
        val timeProvider = timeProvider(zoneOffsetHours = 3)

        val result = localTime.toUtc(timeProvider).epochSecond

        result shouldBe 1664912100L
    }
})