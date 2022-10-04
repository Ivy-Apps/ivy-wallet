package com.ivy.core.ui.time.handling

import com.ivy.core.ui.data.period.MonthUi
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit

sealed interface SelectPeriodEvent {
    data class Monthly(val month: MonthUi) : SelectPeriodEvent

    data class InTheLast(val n: Int, val unit: TimeUnit) : SelectPeriodEvent

    object AllTime : SelectPeriodEvent

    data class CustomRange(val range: TimeRange) : SelectPeriodEvent

    object ResetToCurrentPeriod : SelectPeriodEvent

    object ThisYear : SelectPeriodEvent

    object LastYear : SelectPeriodEvent

    object ShiftForward : SelectPeriodEvent

    object ShiftBackward : SelectPeriodEvent
}