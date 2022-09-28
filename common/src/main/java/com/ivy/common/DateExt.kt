package com.ivy.common


import java.time.*
import java.time.format.DateTimeFormatter


fun timeNowLocal(): LocalDateTime = LocalDateTime.now()

fun timeNowUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun dateNowUTC(): LocalDate = LocalDate.now(ZoneOffset.UTC)

fun dateNowLocal(): LocalDate = LocalDate.now()

@Deprecated("don't use")
fun startOfDayNowUTC(): LocalDateTime = dateNowUTC().atStartOfDay()

fun Long.epochSecondToDateTime(): LocalDateTime =
    LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)

fun LocalDateTime.toEpochSeconds() = this.toEpochSecond(ZoneOffset.UTC)

@Deprecated("don't use")
fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

fun LocalDateTime.toEpochMilli(): Long = millis()

fun LocalDateTime.millis() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

@Deprecated("don't use")
fun LocalDate.formatDateOnly(): String = this.format("MMM. dd")

fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

@Deprecated("don't use")
fun LocalDateTime.convertUTCtoLocal(zone: ZoneId = ZoneOffset.systemDefault()): LocalDateTime {
    return this.convertUTCto(zone)
}

@Deprecated("don't use")
fun LocalDateTime.convertUTCto(zone: ZoneId): LocalDateTime {
    return plusSeconds(atZone(zone).offset.totalSeconds.toLong())
}


@Deprecated("don't use")
fun LocalDateTime.convertLocalToUTC(): LocalDateTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}

fun startOfMonth(date: LocalDate): LocalDateTime =
    date.withDayOfMonth(1).atStartOfDay()

fun endOfMonth(date: LocalDate): LocalDateTime =
    date.withDayOfMonth(date.lengthOfMonth()).atEndOfDay()

fun LocalDate.atEndOfDay(): LocalDateTime =
    this.atTime(23, 59, 59)

fun beginningOfIvyTime(): LocalDateTime = LocalDateTime.of(1990, 1, 1, 0, 0)

fun endOfIvyTime(): LocalDateTime = LocalDateTime.of(2050, 1, 1, 0, 0)

fun LocalDate.withDayOfMonthSafe(targetDayOfMonth: Int): LocalDate {
    val maxDayOfMonth = this.lengthOfMonth()
    return this.withDayOfMonth(
        if (targetDayOfMonth > maxDayOfMonth) maxDayOfMonth else targetDayOfMonth
    )
}