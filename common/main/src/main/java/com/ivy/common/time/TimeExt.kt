package com.ivy.common.time

import java.time.*
import java.time.format.DateTimeFormatter

// region Conversions
fun local(instant: Instant): LocalDateTime =
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

fun utc(time: LocalDateTime): Instant = time.toInstant(ZoneOffset.UTC)

fun LocalDateTime.toEpochMilli(): Long = utc(this).toEpochMilli()
fun LocalDateTime.toEpochSeconds() = this.toEpochSecond(ZoneOffset.UTC)
// endregion

// region Formatting
fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))
// endregion

// region Day
// .atStartOfDay() is already built-in in LocalDate

fun LocalDate.atEndOfDay(): LocalDateTime =
    this.atTime(23, 59, 59)
// endregion

// region Week
// TODO
// endregion

// region Month
fun startOfMonth(date: LocalDate): LocalDateTime =
    date.withDayOfMonth(1).atStartOfDay()

fun endOfMonth(date: LocalDate): LocalDateTime =
    date.withDayOfMonth(date.lengthOfMonth()).atEndOfDay()

fun LocalDate.withDayOfMonthSafe(targetDayOfMonth: Int): LocalDate {
    val maxDayOfMonth = this.lengthOfMonth()
    return this.withDayOfMonth(
        if (targetDayOfMonth > maxDayOfMonth) maxDayOfMonth else targetDayOfMonth
    )
}
// endregion

// region Year
// TODO
// endregion

// region All-time
fun beginningOfIvyTime(): LocalDateTime = LocalDateTime.of(1990, 1, 1, 0, 0)

fun endOfIvyTime(): LocalDateTime = LocalDateTime.of(2050, 1, 1, 0, 0)
// endregion

// region Delete:
@Deprecated("Use `TimeProvider` instead!")
fun timeNow(): LocalDateTime = LocalDateTime.now()

@Deprecated("LocalDate and LocalTime must be indeed local!")
fun dateNowUTC(): LocalDate = LocalDate.now(ZoneOffset.UTC)

@Deprecated("LocalDate and LocalTime must be indeed local!")
fun dateNowLocal(): LocalDate = LocalDate.now()

@Deprecated("don't use")
fun startOfDayNowUTC(): LocalDateTime = dateNowUTC().atStartOfDay()

@Deprecated("Don't use!")
fun Long.epochSecondToDateTime(): LocalDateTime =
    LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)

@Deprecated("don't use")
fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

@Deprecated("don't use")
fun LocalDate.formatDateOnly(): String = this.format("MMM. dd")

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
    val offset = timeNow().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}
// endregion
