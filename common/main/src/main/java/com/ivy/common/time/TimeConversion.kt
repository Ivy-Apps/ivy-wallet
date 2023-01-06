package com.ivy.common.time

import com.ivy.common.time.provider.TimeProvider
import java.time.Instant
import java.time.LocalDateTime

fun Instant.toLocal(timeProvider: TimeProvider): LocalDateTime =
    LocalDateTime.ofInstant(this, timeProvider.zoneId())

fun LocalDateTime.toUtc(timeProvider: TimeProvider): Instant = toInstant(
    timeProvider.zoneId().rules.getOffset(this)
)

fun LocalDateTime.toEpochMilli(timeProvider: TimeProvider): Long =
    toUtc(timeProvider).toEpochMilli()

fun LocalDateTime.toEpochSeconds(timeProvider: TimeProvider) =
    toUtc(timeProvider).epochSecond