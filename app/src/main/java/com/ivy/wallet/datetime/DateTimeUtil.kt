package com.ivy.wallet.datetime

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Instant.toLocal(): ZonedDateTime {
    return ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
}

fun ZonedDateTime.format(style: FormatStyle = FormatStyle.LONG): String {
    return format(DateTimeFormatter.ofLocalizedDateTime(style))
}
