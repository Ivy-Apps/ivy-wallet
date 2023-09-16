package com.ivy.core.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Instant.toLocal(): ZonedDateTime {
    return ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
}

fun ZonedDateTime.format(style: FormatStyle = FormatStyle.LONG): String {
    return format(DateTimeFormatter.ofLocalizedDateTime(style))
}


// TODO: Legacy code, might be broken
fun Long.epochMilliToDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDateTime()

// TODO: Legacy code, might be broken
fun LocalDateTime.toEpochMilli(): Long = this.toInstant(ZoneOffset.UTC).toEpochMilli()
