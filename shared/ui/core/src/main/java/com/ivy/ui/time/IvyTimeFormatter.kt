package com.ivy.ui.time

import com.ivy.base.ResourceProvider
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class IvyTimeFormatter @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val timeProvider: TimeProvider,
    private val converter: TimeConverter,
) : TimeFormatter {

    override fun LocalDateTime.format(style: TimeFormatter.Style): String {
        val today = timeProvider.localNow().toLocalDate()
        val isThisYear = today.year == this.year

        return when (style) {
            is TimeFormatter.Style.DateOnly -> formatDateOnly(
                today = today,
                isThisYear = isThisYear,
                includeWeekDay = style.includeWeekDay
            )

            is TimeFormatter.Style.DateTime -> formatDateTime(
                today = today,
                isThisYear = isThisYear,
                includeWeekDay = style.includeWeekDay
            )

        }
    }

    private fun LocalDateTime.formatDateOnly(
        today: LocalDate,
        isThisYear: Boolean,
        includeWeekDay: Boolean
    ): String {
        val patternNoWeekDay = "dd MMM"
        return when (this.toLocalDate()) {
            today -> {
                resourceProvider.getString(R.string.today_date, this.formatLocal(patternNoWeekDay))
            }

            today.minusDays(1) -> {
                resourceProvider.getString(
                    R.string.yesterday_date,
                    this.formatLocal(patternNoWeekDay)
                )
            }

            today.plusDays(1) -> {
                resourceProvider.getString(
                    R.string.tomorrow_date,
                    this.formatLocal(patternNoWeekDay)
                )
            }

            else -> {
                if (includeWeekDay) {
                    if (isThisYear) {
                        this.formatLocal("EEE, dd MMM")
                    } else {
                        this.formatLocal("dd MMM, yyyy")
                    }
                } else {
                    if (isThisYear) {
                        this.formatLocal(patternNoWeekDay)
                    } else {
                        this.formatLocal("dd MMM, yyyy")
                    }
                }
            }
        }
    }

    private fun LocalDateTime.formatDateTime(
        today: LocalDate,
        isThisYear: Boolean,
        includeWeekDay: Boolean
    ): String {
        val datePattern = if (includeWeekDay) "EEE, dd MMM" else "dd MMM"

        return when (this.toLocalDate()) {
            today -> {
                resourceProvider.getString(R.string.today_date, this.formatLocal(datePattern))
            }

            today.minusDays(1) -> {
                resourceProvider.getString(R.string.yesterday_date, this.formatLocal(datePattern))
            }

            today.plusDays(1) -> {
                resourceProvider.getString(R.string.tomorrow_date, this.formatLocal(datePattern))
            }

            else -> {
                if (isThisYear) {
                    this.formatLocal(datePattern)
                } else {
                    this.formatLocal("dd MMM, yyyy")
                }
            }
        }
    }

    override fun LocalTime.format(): String {
        return this.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    override fun Instant.formatLocal(style: TimeFormatter.Style): String {
        val localDateTime = with(converter) { this@formatLocal.toLocalDateTime() }
        return localDateTime.format(style)
    }

    override fun Instant.formatUtc(style: TimeFormatter.Style): String {
        val localDateTime = this.atZone(ZoneOffset.UTC).toLocalDateTime()
        return localDateTime.format(style)
    }

    private fun LocalDateTime.formatLocal(pattern: String): String {
        return this.format(DateTimeFormatter.ofPattern(pattern))
    }
}