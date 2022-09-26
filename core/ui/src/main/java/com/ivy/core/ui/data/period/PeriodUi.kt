package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.Period

@Immutable
data class PeriodUi(
    val period: Period.FromTo,
    val fromText: String,
    val toText: String,
)