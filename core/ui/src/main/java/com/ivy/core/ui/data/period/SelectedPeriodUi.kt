package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.Period
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(
        val month: MonthUi
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        val n: Int,
        val unit: TimeUnit
    ) : SelectedPeriodUi

    @Immutable
    object AllTime : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        val period: Period.FromTo,
    ) : SelectedPeriodUi
}