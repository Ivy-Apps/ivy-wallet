package com.ivy.base.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

interface TimeProvider {
    fun getZoneId(): ZoneId
    fun utcNow(): Instant
    fun localNow(): LocalDateTime
    fun localDateNow(): LocalDate
    fun localTimeNow(): LocalTime
}
