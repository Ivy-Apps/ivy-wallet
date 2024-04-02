package com.ivy.base.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun Instant.convertToLocal(): ZonedDateTime {
    return atZone(ZoneId.systemDefault())
}
fun Instant.asLocalDateTime(): LocalDateTime = this.convertToLocal().toLocalDateTime()

fun LocalDateTime.asInstant() : Instant = this.atZone(ZoneId.systemDefault()).toInstant()