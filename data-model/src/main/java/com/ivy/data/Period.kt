package com.ivy.data

import java.time.LocalDateTime

sealed class Period {
    data class FromTo(val from: LocalDateTime, val to: LocalDateTime) : Period()
    data class Before(val to: LocalDateTime) : Period()
    data class After(val from: LocalDateTime) : Period()
}