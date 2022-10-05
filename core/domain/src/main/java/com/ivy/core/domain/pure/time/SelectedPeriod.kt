package com.ivy.core.domain.pure.time

import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange

fun SelectedPeriod.range(): TimeRange = when (this) {
    is SelectedPeriod.AllTime -> range
    is SelectedPeriod.CustomRange -> range
    is SelectedPeriod.InTheLast -> range
    is SelectedPeriod.Monthly -> range
}