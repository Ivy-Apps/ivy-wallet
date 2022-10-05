package com.ivy.common.time

import com.ivy.common.time.provider.DeviceTimeProvider
import java.time.*
import java.time.format.DateTimeFormatter

// region Formatting
fun LocalDateTime.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.format(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))
// endregion


// region All-time
fun beginningOfIvyTime(): LocalDateTime =
    LocalDateTime.of(1990, 1, 1, 0, 0)

fun endOfIvyTime(): LocalDateTime =
    LocalDateTime.of(2050, 1, 1, 0, 0)
// endregion

// region Deprecated (will be deleted)
@Deprecated("Don't use! Use TimeProvider via DI instead!")
fun deviceTimeProvider() = DeviceTimeProvider()

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
