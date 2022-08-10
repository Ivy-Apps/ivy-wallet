package com.ivy.data

import java.time.LocalDateTime

sealed class Period {
    data class FromTo(val from: LocalDateTime, val to: LocalDateTime) : Period()
    data class Before(val data: LocalDateTime) : Period()
    data class After(val data: LocalDateTime) : Period()
}