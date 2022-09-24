package com.ivy.core.ui.time

import android.content.Context
import com.ivy.base.R
import com.ivy.common.dateNowUTC
import com.ivy.common.formatLocal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.formatNicely(
    context: Context,
    includeWeekDay: Boolean = true,
): String {
    val zone: ZoneId = ZoneOffset.systemDefault()
    val today = dateNowUTC()
    val isThisYear = today.year == this.year

    val patternNoWeekDay = "dd MMM"

    if (!includeWeekDay) {
        return if (isThisYear) {
            this.formatLocal(patternNoWeekDay)
        } else {
            this.formatLocal("dd MMM, yyyy")
        }
    }

    return when (this.toLocalDate()) {
        today -> {
            context.getString(
                R.string.today_date,
                this.formatLocal(patternNoWeekDay)
            )
        }
        today.minusDays(1) -> {
            context.getString(
                R.string.yesterday_date,
                this.formatLocal(patternNoWeekDay)
            )
        }
        today.plusDays(1) -> {
            context.getString(
                R.string.tomorrow_date,
                this.formatLocal(patternNoWeekDay)
            )
        }
        else -> {
            if (isThisYear) {
                this.formatLocal("EEE, dd MMM")
            } else {
                this.formatLocal("dd MMM, yyyy")
            }
        }
    }
}