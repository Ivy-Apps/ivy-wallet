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

fun startOfDayNowUTC() = dateNowUTC().atStartOfDay()

fun endOfDayNowUTC() = dateNowUTC().atEndOfDay()

fun Long.epochSecondToDateTime(): LocalDateTime =
    LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)

fun LocalDateTime.toEpochSeconds() = this.toEpochSecond(ZoneOffset.UTC)

fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

fun LocalDateTime.toEpochMilli(): Long = millis()

fun LocalDateTime.millis() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

fun LocalDate.formatDateOnly(): String = this.formatLocal("MMM. dd", ZoneOffset.systemDefault())

fun LocalDate.formatDateOnlyWithYear(): String =
    this.formatLocal("dd MMM, yyyy", ZoneOffset.systemDefault())


fun LocalDate.formatDateWeekDay(): String =
    this.formatLocal("EEE, dd MMM", ZoneOffset.systemDefault())

fun LocalDate.formatDateWeekDayLong(): String =
    this.formatLocal("EEEE, dd MMM", ZoneOffset.systemDefault())


fun LocalDateTime.formatLocal(
    pattern: String = "dd MMM yyyy, HH:mm",
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val localDateTime = this.convertUTCtoLocal(zone)
    return localDateTime.atZone(zone).format(
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(Locale.getDefault())
            .withZone(zone) //this is if you want to display the Zone in the pattern
    )
}

fun LocalDateTime.format(
    pattern: String
): String {
    return this.format(
        DateTimeFormatter.ofPattern(pattern)
    )
}

fun LocalDateTime.convertUTCtoLocal(zone: ZoneId = ZoneOffset.systemDefault()): LocalDateTime {
    return this.convertUTCto(zone)
}

fun LocalDateTime.convertUTCto(zone: ZoneId): LocalDateTime {
    return plusSeconds(atZone(zone).offset.totalSeconds.toLong())
}

fun LocalTime.convertLocalToUTC(): LocalTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}

fun LocalTime.convertUTCToLocal(): LocalTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.plusSeconds(offset)
}

fun LocalDateTime.convertLocalToUTC(): LocalDateTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}

// The timepicker returns time in UTC, but the date picker returns date in LocalTimeZone
// hence use this method to get both date & time in UTC
fun getTrueDate(date: LocalDate, time: LocalTime, convert: Boolean = true): LocalDateTime {
    val timeLocal = if (convert) time.convertUTCToLocal() else time

    return timeNowUTC()
        .withYear(date.year)
        .withMonth(date.monthValue)
        .withDayOfMonth(date.dayOfMonth)
        .withHour(timeLocal.hour)
        .withMinute(timeLocal.minute)
        .withSecond(0)
        .withNano(0)
        .convertLocalToUTC()
}


fun LocalDate.formatLocal(
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
    date.withDayOfMonth(1).atStartOfDay().convertLocalToUTC()

fun endOfMonth(date: LocalDate): LocalDateTime =
    date.withDayOfMonth(date.lengthOfMonth()).atEndOfDay().convertLocalToUTC()

fun LocalDate.atEndOfDay(): LocalDateTime =
    this.atTime(23, 59, 59)

/**
 * +1 day so things won't fck up with Long overflow
 */
fun beginningOfIvyTime(): LocalDateTime = LocalDateTime.now().minusYears(99)

fun toIvyFutureTime(): LocalDateTime = timeNowUTC().plusYears(30)

fun LocalDate.withDayOfMonthSafe(targetDayOfMonth: Int): LocalDate {
    val maxDayOfMonth = this.lengthOfMonth()
    return this.withDayOfMonth(
        if (targetDayOfMonth > maxDayOfMonth) maxDayOfMonth else targetDayOfMonth
    )
}