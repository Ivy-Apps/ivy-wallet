package com.ivy.common.time

import android.content.Context
import com.ivy.common.R
import com.ivy.common.time.provider.DeviceTimeProvider
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

// region Formatting
fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalTime.deviceFormat(
    appContext: Context
): String = if (uses24HourFormat(appContext))
    format("HH:mm") else format("hh:mm a")
// endregion

fun uses24HourFormat(
    appContext: Context,
): Boolean = android.text.format.DateFormat.is24HourFormat(appContext)

fun LocalDate.contextText(
    alwaysShowWeekday: Boolean,
    getString: (Int) -> String
): String {
    val today = LocalDate.now()
    val alwaysWeekdayText = if (alwaysShowWeekday)
        " (${this.format(pattern = "EEEE")})" else ""
    return when (this) {
        today -> {
            getString(R.string.today) + alwaysWeekdayText
        }
        today.minusDays(1) -> {
            getString(R.string.yesterday) + alwaysWeekdayText
        }
        today.plusDays(1) -> {
            getString(R.string.tomorrow) + alwaysWeekdayText
        }
        else -> {
            this.format(pattern = "EEEE")
        }
    }
}


// region All-time
fun beginningOfIvyTime(): LocalDateTime =
    LocalDateTime.of(1990, 1, 1, 0, 0)

fun endOfIvyTime(): LocalDateTime =
    LocalDateTime.of(2050, 1, 1, 0, 0)
// endregion

fun LocalDate.dateId() = format("dd-MM-yyyy")

fun deviceTimeProvider() = DeviceTimeProvider()


// region Deprecated (will be deleted)
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
