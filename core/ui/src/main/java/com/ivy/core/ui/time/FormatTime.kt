package com.ivy.core.ui.time

import android.content.Context
import com.ivy.common.time.format
import com.ivy.common.time.provider.TimeProvider
import com.ivy.resources.R
import java.time.LocalDateTime

fun LocalDateTime.formatNicely(
    context: Context,
    timeProvider: TimeProvider,
    includeWeekDay: Boolean = true,
): String {
    val today = timeProvider.dateNow()
    val isThisYear = today.year == this.year

    val patternNoWeekDay = "dd MMM"

    if (!includeWeekDay) {
        return if (isThisYear) {
            this.format(patternNoWeekDay)
        } else {
            this.format("dd MMM, yyyy")
        }
    }

    return when (this.toLocalDate()) {
        today -> {
            context.getString(
                R.string.today_date,
                this.format(patternNoWeekDay)
            )
        }
        today.minusDays(1) -> {
            context.getString(
                R.string.yesterday_date,
                this.format(patternNoWeekDay)
            )
        }
        today.plusDays(1) -> {
            context.getString(
                R.string.tomorrow_date,
                this.format(patternNoWeekDay)
            )
        }
        else -> {
            if (isThisYear) {
                this.format("EEE, dd MMM")
            } else {
                this.format("dd MMM, yyyy")
            }
        }
    }
}