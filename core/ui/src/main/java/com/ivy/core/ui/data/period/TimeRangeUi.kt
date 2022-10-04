package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.common.time.timeNow
import com.ivy.data.time.TimeRange

@Immutable
data class TimeRangeUi(
    val range: TimeRange,
    val fromText: String,
    val toText: String,
)

fun dummyRangeUi() = TimeRangeUi(
    range = TimeRange(timeNow(), timeNow()),
    fromText = "",
    toText = ""
)