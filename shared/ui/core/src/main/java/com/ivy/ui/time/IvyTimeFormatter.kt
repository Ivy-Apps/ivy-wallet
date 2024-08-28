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
            // Today
            today -> resourceProvider.getString(
                R.string.today_date,
                this.format(pattern = patternNoWeekDay)
            )

            // Yesterday
            today.minusDays(1) -> {
                resourceProvider.getString(
                    R.string.yesterday_date,
                    this.format(pattern = patternNoWeekDay)
                )
            }

            // Tomorrow
            today.plusDays(1) -> resourceProvider.getString(
                R.string.tomorrow_date,
                this.format(pattern = patternNoWeekDay)
            )

            else -> {
                if (includeWeekDay) {
                    if (isThisYear) {
                        this.format(pattern = "EEE, dd MMM")
                    } else {
                        this.format(pattern = "dd MMM, yyyy")
                    }
                } else {
                    if (isThisYear) {
                        this.format(pattern = patternNoWeekDay)
                    } else {
                        this.format(pattern = "dd MMM, yyyy")
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
            // Today
            today -> resourceProvider.getString(R.string.today_date, this.format(datePattern))

            // Yesterday
            today.minusDays(1) -> resourceProvider.getString(
                R.string.yesterday_date,
                this.format(pattern = datePattern)
            )

            // Tomorrow
            today.plusDays(1) -> resourceProvider.getString(
                R.string.tomorrow_date,
                this.format(pattern = datePattern)
            )

            else -> {
                if (isThisYear) {
                    this.format(pattern = datePattern)
                } else {
                    this.format(pattern = "dd MMM, yyyy")
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

    private fun LocalDateTime.format(pattern: String): String {
        return this.format(DateTimeFormatter.ofPattern(pattern))
    }
}