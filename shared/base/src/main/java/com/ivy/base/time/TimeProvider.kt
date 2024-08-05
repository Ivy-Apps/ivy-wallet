package com.ivy.base.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

interface TimeProvider {
    fun getZoneId(): ZoneId
    fun utcNow(): Instant
    fun localNow(): LocalDateTime
}
