package com.ivy.base.time.impl

import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class StandardTimeConvert @Inject constructor(
    private val timeZoneProvider: TimeProvider
) : TimeConverter {

    override fun Instant.toLocalDateTime(): LocalDateTime {
        val zoneId = timeZoneProvider.getZoneId()
        return this.atZone(zoneId).toLocalDateTime()
    }

    override fun Instant.toLocalDate(): LocalDate {
        val zoneId = timeZoneProvider.getZoneId()
        return this.atZone(zoneId).toLocalDate()
    }

    override fun LocalDateTime.toUTC(): Instant {
        val zoneId = timeZoneProvider.getZoneId()
        return this.atZone(zoneId).toInstant()
    }
}