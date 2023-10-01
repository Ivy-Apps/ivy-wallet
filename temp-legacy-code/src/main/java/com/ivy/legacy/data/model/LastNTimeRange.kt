package com.ivy.legacy.data.model

import androidx.compose.runtime.Immutable
import com.ivy.legacy.forDisplay
import com.ivy.legacy.incrementDate
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.data.model.IntervalType
import java.time.LocalDateTime

@Immutable
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
