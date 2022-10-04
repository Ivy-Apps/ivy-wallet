package com.ivy.common.time

import java.time.LocalDate
import java.time.LocalDateTime

interface TimeProvider {
    fun timeNow(): LocalDateTime
    fun dateNow(): LocalDate
}