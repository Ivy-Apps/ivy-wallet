package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    val periodBtnText: String
    val rangeUi: TimeRangeUi

    @Immutable
    data class Monthly(
        override val periodBtnText: String,
        val month: MonthUi,
        override val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        override val periodBtnText: String,
        val n: Int,
        val unit: TimeUnit,
        override val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class AllTime(
        override val periodBtnText: String,
        override val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        override val periodBtnText: String,
        override val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi
}