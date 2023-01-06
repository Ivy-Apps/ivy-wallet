package com.ivy.common.time.provider

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface TimeProvider {
    fun timeNow(): LocalDateTime
    fun dateNow(): LocalDate
    fun zoneId(): ZoneId
}