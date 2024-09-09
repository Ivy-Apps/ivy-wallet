package com.ivy.base.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface TimeConverter {
    fun Instant.toLocalDateTime(): LocalDateTime
    fun Instant.toLocalDate(): LocalDate
    fun Instant.toLocalTime(): LocalTime

    fun LocalDateTime.toUTC(): Instant
}