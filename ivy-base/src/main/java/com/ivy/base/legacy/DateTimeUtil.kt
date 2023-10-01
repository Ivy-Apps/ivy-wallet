package com.ivy.base.legacy

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Deprecated("Legacy")
fun ZonedDateTime.format(style: FormatStyle = FormatStyle.LONG): String {
    return format(DateTimeFormatter.ofLocalizedDateTime(style))
}

@Deprecated("Broken legacy code, will deleted")
fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

@Deprecated("Broken l egacy code, will deleted")
fun LocalDateTime.toEpochMilli(): Long = this.toInstant(ZoneOffset.UTC).toEpochMilli()
