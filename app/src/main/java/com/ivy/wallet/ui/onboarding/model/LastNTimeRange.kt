package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.utils.timeNowUTC
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
