package com.ivy.core.ui.time

import com.ivy.base.R
import com.ivy.common.dateNowUTC
import com.ivy.common.formatLocal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.formatNicely(
    noWeekDay: Boolean = false,
    zone: ZoneId = ZoneOffset.systemDefault()
): String {
    val today = dateNowUTC()
    val isThisYear = today.year == this.year

    val patternNoWeekDay = "dd MMM"

    if (noWeekDay) {
        return if (isThisYear) {
            this.formatLocal(patternNoWeekDay)
        } else {
            this.formatLocal("dd MMM, yyyy")
        }
    }

    return when (this.toLocalDate()) {
        today -> {
            com.ivy.core.ui.temp.stringRes(
                R.string.today_date,
                this.formatLocal(patternNoWeekDay, zone)
            )
        }
        today.minusDays(1) -> {
            com.ivy.core.ui.temp.stringRes(
                R.string.yesterday_date,
                this.formatLocal(patternNoWeekDay, zone)
            )
        }
        today.plusDays(1) -> {
            com.ivy.core.ui.temp.stringRes(
                R.string.tomorrow_date,
                this.formatLocal(patternNoWeekDay, zone)
            )
        }
        else -> {
            if (isThisYear) {
                this.formatLocal("EEE, dd MMM", zone)
            } else {
                this.formatLocal("dd MMM, yyyy", zone)
            }
        }
    }
}