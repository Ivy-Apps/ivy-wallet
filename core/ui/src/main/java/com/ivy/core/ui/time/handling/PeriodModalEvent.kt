package com.ivy.core.ui.time.handling

import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.data.time.Month
import com.ivy.data.time.Period
import com.ivy.data.time.TimeUnit

sealed interface PeriodModalEvent {
    data class SetPeriod(val period: SelectedPeriodUi) : PeriodModalEvent

    data class Monthly(val month: Month) : PeriodModalEvent

    data class InTheLast(val n: Int, val unit: TimeUnit) : PeriodModalEvent

    object AllTime : PeriodModalEvent

    data class CustomRange(val period: Period.FromTo) : PeriodModalEvent
}