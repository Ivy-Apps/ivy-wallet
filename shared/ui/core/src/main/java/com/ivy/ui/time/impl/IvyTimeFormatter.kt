package com.ivy.ui.time.impl

import com.ivy.base.resource.ResourceProvider
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.ui.R
import com.ivy.ui.time.DevicePreferences
import com.ivy.ui.time.TimeFormatter
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
    private val devicePreferences: DevicePreferences,
) : TimeFormatter {

    override fun LocalDateTime.format(style: TimeFormatter.Style): String {
        val dateTime = this
        val today = timeProvider.localDateNow()
        val relativeDay = if (style.includeWeekDay) {
            this.getRelativeDay(today)
        } else {
            null
        }
        val pattern = buildString {
            if (relativeDay == null && style.includeWeekDay) {
                append("EEE, ")
            }
            append("MMM d")
            if (dateTime.year != today.year) {
                append(" yyyy")
            }
            if (style is TimeFormatter.Style.DateAndTime) {
                append(" ")
                append(localeTimeFormat())
            }
        }
        val formatted = dateTime.format(
            DateTimeFormatter.ofPattern(pattern, devicePreferences.locale())
        )
        val prefix = when (relativeDay) {
            RelativeDay.Yesterday -> resourceProvider.getString(R.string.yesterday)
            RelativeDay.Today -> resourceProvider.getString(R.string.today)
            RelativeDay.Tomorrow -> resourceProvider.getString(R.string.tomorrow)
            else -> null
        }

        return if (prefix != null) {
            "$prefix, $formatted"
        } else {
            formatted
        }
    }

    override fun LocalTime.format(): String = this.format(
        DateTimeFormatter.ofPattern(localeTimeFormat(), devicePreferences.locale())
    )

    private fun localeTimeFormat(): String = if (devicePreferences.is24HourFormat()) {
        "HH:mm"
    } else {
        "h:mm a"
    }

    override fun Instant.formatLocal(style: TimeFormatter.Style): String {
        val instant = this
        val localDateTime = with(converter) { instant.toLocalDateTime() }
        return localDateTime.format(style)
    }

    override fun Instant.formatUtc(style: TimeFormatter.Style): String {
        val localDateTime = this.atZone(ZoneOffset.UTC).toLocalDateTime()
        return localDateTime.format(style)
    }

    private fun LocalDateTime.getRelativeDay(today: LocalDate): RelativeDay? {
        val date = this.toLocalDate()
        return when (date) {
            today -> RelativeDay.Today
            today.plusDays(1) -> RelativeDay.Tomorrow
            today.minusDays(1) -> RelativeDay.Yesterday
            else -> null
        }
    }

    enum class RelativeDay {
        Yesterday, Today, Tomorrow,
    }
}