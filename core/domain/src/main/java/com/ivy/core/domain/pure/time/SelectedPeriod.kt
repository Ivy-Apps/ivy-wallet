package com.ivy.core.domain.pure.time

import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod

fun SelectedPeriod.period(): Period = when (this) {
    is SelectedPeriod.AllTime -> period
    is SelectedPeriod.CustomRange -> period
    is SelectedPeriod.InTheLast -> period
    is SelectedPeriod.Monthly -> period
}