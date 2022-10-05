package com.ivy.common.time

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface TimeProvider {
    fun timeNow(): LocalDateTime
    fun dateNow(): LocalDate
    fun zoneId(): ZoneId
}