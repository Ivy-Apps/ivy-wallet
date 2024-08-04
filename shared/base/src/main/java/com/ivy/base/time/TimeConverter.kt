package com.ivy.base.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

interface TimeConverter {
    fun Instant.toLocalDateTime(): LocalDateTime
    fun Instant.toLocalDate(): LocalDate

    fun LocalDateTime.toUTC(): Instant
}