package com.ivy.core.ui.time.handling

import com.ivy.core.ui.data.period.MonthUi
import com.ivy.data.time.Period
import com.ivy.data.time.TimeUnit

sealed interface PeriodModalEvent {
    data class Monthly(val month: MonthUi) : PeriodModalEvent

    data class InTheLast(val n: Int, val unit: TimeUnit) : PeriodModalEvent

    object AllTime : PeriodModalEvent

    data class CustomRange(val period: Period.FromTo) : PeriodModalEvent

    object ResetToCurrentPeriod : PeriodModalEvent

    object ThisYear : PeriodModalEvent

    object LastYear : PeriodModalEvent
}