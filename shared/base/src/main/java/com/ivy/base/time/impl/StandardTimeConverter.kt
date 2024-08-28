package com.ivy.base.time.impl

import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class StandardTimeConverter @Inject constructor(
    private val timeZoneProvider: TimeProvider
) : TimeConverter {

    override fun Instant.toLocalDateTime(): LocalDateTime = try {
        val zoneId = timeZoneProvider.getZoneId()
        this.atZone(zoneId).toLocalDateTime()
    } catch (e: DateTimeException) {
        // This happens when we overflow MIN/MAX for LocalDateTime
        if (this > Instant.EPOCH) {
            LocalDateTime.MAX.minusYears(10)
        } else {
            LocalDateTime.MIN.plusYears(10)
        }
    }

    override fun Instant.toLocalDate(): LocalDate = try {
        val zoneId = timeZoneProvider.getZoneId()
        this.atZone(zoneId).toLocalDate()
    } catch (e: DateTimeException) {
        // This happens when we overflow MIN/MAX for LocalDate
        if (this > Instant.EPOCH) {
            LocalDate.MAX.minusYears(10)
        } else {
            LocalDate.MIN.plusYears(10)
        }
    }

    override fun LocalDateTime.toUTC(): Instant {
        val zoneId = timeZoneProvider.getZoneId()
        return this.atZone(zoneId).toInstant()
    }
}