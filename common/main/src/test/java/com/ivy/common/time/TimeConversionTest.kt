package com.ivy.common.time

import com.ivy.common.time.provider.TimeProvider
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.forAll
import java.time.*

class TimeConversionTest : StringSpec({
    fun timeProvider(zoneOffsetHours: Int) = object : TimeProvider {
        override fun timeNow(): LocalDateTime = LocalDateTime.now()

        override fun dateNow(): LocalDate = timeNow().toLocalDate()

        override fun zoneId(): ZoneId =
            ZoneId.ofOffset("GMT", ZoneOffset.ofHours(zoneOffsetHours))
    }

    "convert local time to utc" {
        val localTime = LocalDateTime.of(
            2022, 10, 4,
            22, 35, 0
        )
        val timeProvider = timeProvider(zoneOffsetHours = 3)

        val result = localTime.toUtc(timeProvider).epochSecond

        result shouldBe 1664912100L
    }

    "convert utc to local time" {
        // UTC: October 4, 2022 19:39:00
        val utc = Instant.ofEpochSecond(1664912340L)

        val sofiaTime = utc.toLocal(timeProvider(zoneOffsetHours = 3))
        val newYorkTime = utc.toLocal(timeProvider(zoneOffsetHours = -4))

        sofiaTime shouldBe LocalDateTime.of(
            2022, 10, 4,
            22, 39, 0
        )
        newYorkTime shouldBe LocalDateTime.of(
            2022, 10, 4,
            15, 39, 0
        )
    }

    "local <> utc property" {
        forAll(
            Arb.localDateTime(), Arb.int(min = -18, max = 18)
        ) { local, zoneOffset ->
            val timeProvider = timeProvider(zoneOffsetHours = zoneOffset)
            val utc = local.toUtc(timeProvider)
            val localRestored = utc.toLocal(timeProvider)
            local == localRestored
        }
    }
})