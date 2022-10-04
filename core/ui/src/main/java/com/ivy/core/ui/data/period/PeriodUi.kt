package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.common.timeNowLocal
import com.ivy.data.time.TimeRange

@Immutable
data class PeriodUi(
    val range: TimeRange,
    val fromText: String,
    val toText: String,
)

fun dummyPeriodUi() = PeriodUi(
    range = TimeRange(timeNowLocal(), timeNowLocal()),
    fromText = "",
    toText = ""
)