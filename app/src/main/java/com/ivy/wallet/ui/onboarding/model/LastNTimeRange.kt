package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.model.IntervalType
import java.time.LocalDateTime

data class LastNTimeRange(
    val periodN: Int,
    val periodType: IntervalType,
) {
    fun fromDate(): LocalDateTime = periodType.incrementDate(
        date = timeNowUTC(),
        intervalN = -periodN.toLong()
    )

    fun forDisplay(): String =
       "$periodN ${periodType.forDisplay(periodN)}"
}