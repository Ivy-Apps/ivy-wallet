package com.ivy.ui.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime

interface TimeFormatter {
    fun LocalDateTime.format(style: Style): String
    fun LocalTime.format(): String
    fun Instant.formatLocal(style: Style): String
    fun Instant.formatUtc(style: Style): String

    sealed interface Style {
        data class DateTime(
            val includeWeekDay: Boolean,
        ): Style

        data class DateOnly(
            val includeWeekDay: Boolean,
        ): Style
    }
}