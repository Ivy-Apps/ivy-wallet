package com.ivy.legacy.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ivy.base.legacy.stringRes
import com.ivy.base.time.TimeConverter
import com.ivy.frp.Total
import com.ivy.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

@Deprecated("Use the TimeProvider interface via DI")
fun timeNowLocal(): LocalDateTime = LocalDateTime.now()

@Deprecated("Use the TimeProvider interface via DI")
fun dateNowLocal(): LocalDate = LocalDate.now()

@Deprecated("Use the TimeProvider interface via DI")
@Total
fun timeNowUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

@Deprecated("Use the TimeProvider interface via DI")
@Total
fun timeUTC(): LocalTime = LocalTime.now(ZoneOffset.UTC)

@Deprecated("Use the TimeProvider interface via DI")
@Total
fun dateNowUTC(): LocalDate = LocalDate.now(ZoneOffset.UTC)

@Deprecated("Use the TimeProvider interface via DI + atStartOfDay()")
fun startOfDayNowUTC(): LocalDateTime = dateNowUTC().atStartOfDay()

fun LocalDateTime.toEpochSeconds() = this.toEpochSecond(ZoneOffset.UTC)

fun LocalDateTime.millis() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

fun LocalDateTime.formatNicely(
    noWeekDay: Boolean = false,
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val today = dateNowUTC()
    val isThisYear = today.year == this.year

    val patternNoWeekDay = "dd MMM"

    if (noWeekDay) {
        return if (isThisYear) {
            this.formatLocal(patternNoWeekDay)
        } else {
            this.formatLocal("dd MMM, yyyy")
        }
    }

    return when (this.toLocalDate()) {
        today -> {
            stringRes(R.string.today_date, this.formatLocal(patternNoWeekDay, zone))
        }

        today.minusDays(1) -> {
            stringRes(R.string.yesterday_date, this.formatLocal(patternNoWeekDay, zone))
        }

        today.plusDays(1) -> {
            stringRes(R.string.tomorrow_date, this.formatLocal(patternNoWeekDay, zone))
        }

        else -> {
            if (isThisYear) {
                this.formatLocal("EEE, dd MMM", zone)
            } else {
                this.formatLocal("dd MMM, yyyy", zone)
            }
        }
    }
}

fun LocalDateTime.getISOFormattedDateTime(): String = this.formatLocal("yyyyMMdd-HHmm")

fun LocalDateTime.formatNicelyWithTime(
    noWeekDay: Boolean = true,
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val today = dateNowUTC()
    val isThisYear = today.year == this.year

    val patternNoWeekDay = "dd MMM HH:mm"

    if (noWeekDay) {
        return if (isThisYear) {
            this.formatLocal(patternNoWeekDay)
        } else {
            this.formatLocal("dd MMM, yyyy HH:mm")
        }
    }

    return when (this.toLocalDate()) {
        today -> {
            stringRes(R.string.today_date, this.formatLocal(patternNoWeekDay, zone))
        }

        today.minusDays(1) -> {
            stringRes(R.string.yesterday_date, this.formatLocal(patternNoWeekDay, zone))
        }

        today.plusDays(1) -> {
            stringRes(R.string.tomorrow, this.formatLocal(patternNoWeekDay, zone))
        }

        else -> {
            if (isThisYear) {
                this.formatLocal("EEE, dd MMM HH:mm", zone)
            } else {
                this.formatLocal("dd MMM, yyyy HH:mm", zone)
            }
        }
    }
}

@Composable
fun LocalDateTime.formatLocalTime(): String {
    val timeFormat = android.text.format.DateFormat.getTimeFormat(LocalContext.current)
    return timeFormat.format(this.millis())
}

fun LocalDate.formatDateOnly(): String = this.formatLocal("MMM. dd", ZoneOffset.systemDefault())

fun LocalDateTime.formatTimeOnly(): String = this.format(DateTimeFormatter.ofPattern("HH:mm"))

fun LocalDate.formatDateOnlyWithYear(): String =
    this.formatLocal("dd MMM, yyyy", ZoneOffset.systemDefault())

fun LocalDate.formatDateWeekDay(): String =
    this.formatLocal("EEE, dd MMM", ZoneOffset.systemDefault())

fun LocalDate.formatDateWeekDayLong(): String =
    this.formatLocal("EEEE, dd MMM", ZoneOffset.systemDefault())

fun LocalDate.formatNicely(
    pattern: String = "EEE, dd MMM",
    patternNoWeekDay: String = "dd MMM",
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val closeDay = closeDay()
    return if (closeDay != null) {
        "$closeDay, ${this.formatLocal(patternNoWeekDay, zone)}"
    } else {
        this.formatLocal(
            pattern,
            zone
        )
    }
}

fun LocalDate.closeDay(): String? {
    val today = dateNowUTC()
    return when (this) {
        today -> {
            stringRes(R.string.today)
        }

        today.minusDays(1) -> {
            stringRes(R.string.yesterday)
        }

        today.plusDays(1) -> {
            stringRes(R.string.tomorrow)
        }

        else -> {
            null
        }
    }
}

fun LocalDateTime.formatLocal(
    pattern: String = "dd MMM yyyy, HH:mm",
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val localDateTime = this.convertUTCtoLocal(zone)
    return localDateTime.atZone(zone).format(
        DateTimeFormatter
            .ofPattern(pattern)
            .withLocale(Locale.getDefault())
            .withZone(zone) // this is if you want to display the Zone in the pattern
    )
}

fun LocalDateTime.format(
    pattern: String
): String {
    return this.format(
        DateTimeFormatter.ofPattern(pattern)
    )
}

@Deprecated("Use the TimeConverter interface via DI")
fun LocalDateTime.convertUTCtoLocal(zone: ZoneId = ZoneOffset.systemDefault()): LocalDateTime {
    return this.convertUTCto(zone)
}

@Deprecated("Use the TimeConverter interface via DI")
fun LocalDateTime.convertUTCto(zone: ZoneId): LocalDateTime {
    return plusSeconds(atZone(zone).offset.totalSeconds.toLong())
}

@Deprecated("Use the TimeConverter interface via DI")
fun LocalTime.convertLocalToUTC(): LocalTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.minusSeconds(offset)
}

@Deprecated("Use the TimeConverter interface via DI")
fun LocalTime.convertUTCToLocal(): LocalTime {
    val offset = timeNowLocal().atZone(ZoneOffset.systemDefault()).offset.totalSeconds.toLong()
    return this.plusSeconds(offset)
}

@Deprecated("Use the TimeConverter interface via DI")
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
            .withZone(zone) // this is if you want to display the Zone in the pattern
    )
}

fun startOfMonth(date: LocalDate, timeConverter: TimeConverter): Instant {
    val startOfMonthLocal = date.withDayOfMonth(1).atStartOfDay()
    return with(timeConverter) { startOfMonthLocal.toUTC() }
}

fun endOfMonth(date: LocalDate, timeConverter: TimeConverter): Instant {
    val endOfMonthLocal = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX)
    return with(timeConverter) { endOfMonthLocal.toUTC() }
}

fun LocalDate.atEndOfDay(): LocalDateTime =
    this.atTime(23, 59, 59)

fun ivyMinTime(): Instant = Instant.ofEpochMilli(Long.MIN_VALUE)
    .plusSeconds(TimeUnit.DAYS.toSeconds(365 * 10))

fun ivyMaxTime(): Instant = Instant.ofEpochMilli(Long.MAX_VALUE)
    .minusSeconds(TimeUnit.DAYS.toSeconds(365 * 10))

fun LocalDate.withDayOfMonthSafe(targetDayOfMonth: Int): LocalDate {
    val maxDayOfMonth = this.lengthOfMonth()
    return this.withDayOfMonth(
        if (targetDayOfMonth > maxDayOfMonth) maxDayOfMonth else targetDayOfMonth
    )
}
