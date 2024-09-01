package com.ivy.base.time.impl

import com.ivy.base.time.TimeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class TestTimeConverter : TimeConverter {
    private val utcZoneId = ZoneId.of("UTC")

    override fun Instant.toLocalDateTime(): LocalDateTime {
        return this.atZone(utcZoneId).toLocalDateTime()
    }

    override fun Instant.toLocalDate(): LocalDate {
        return this.atZone(utcZoneId).toLocalDate()
    }

    override fun Instant.toLocalTime(): LocalTime {
        return this.atZone(utcZoneId).toLocalTime()
    }

    override fun LocalDateTime.toUTC(): Instant {
        return this.atZone(utcZoneId).toInstant()
    }
}