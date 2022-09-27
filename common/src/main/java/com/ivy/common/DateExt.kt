package com.ivy.common


import com.ivy.frp.Total
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


fun timeNowLocal() = LocalDateTime.now()

@Total
fun timeNowUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

@Total
fun dateNowUTC(): LocalDate = LocalDate.now(ZoneOffset.UTC)

fun dateNowLocal(): LocalDate = LocalDate.now()

fun startOfDayNowUTC() = dateNowUTC().atStartOfDay()

fun Long.epochSecondToDateTime(): LocalDateTime =
    LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)

fun LocalDateTime.toEpochSeconds() = this.toEpochSecond(ZoneOffset.UTC)

fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

fun LocalDateTime.toEpochMilli(): Long = millis()

fun LocalDateTime.millis() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

fun LocalDate.formatDateOnly(): String = this.formatPattern("MMM. dd", ZoneOffset.systemDefault())

@Deprecated("use LocalDateTime.format(String)")
fun LocalDateTime.formatPattern(pattern: String): String {
    val zone = ZoneOffset.systemDefault()
    val localDateTime = this.convertUTCtoLocal(zone)
    return localDateTime.atZone(zone).format(
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(Locale.getDefault())
            .withZone(zone) //this is only if you want to display the Zone in the pattern
    )
}

fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDateTime.convertUTCtoLocal(zone: ZoneId = ZoneOffset.systemDefault()): LocalDateTime {
    return this.convertUTCto(zone)
}

fun LocalDateTime.convertUTCto(zone: ZoneId): LocalDateTime {
    return plusSeconds(atZone(zone).offset.totalSeconds.toLong())
}

fun LocalTime.convertUTCToLocal(): LocalTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.plusSeconds(offset)
}

fun LocalDateTime.convertLocalToUTC(): LocalDateTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}

fun LocalDate.formatPattern(
    pattern: String = "dd MMM yyyy",
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    return this.format(
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(Locale.getDefault())
            .withZone(zone) //this is if you want to display the Zone in the pattern
    )
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