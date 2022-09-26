package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.Period
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(
        val text: String,
        val month: MonthUi
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        val text: String,
        val n: Int,
        val unit: TimeUnit
    ) : SelectedPeriodUi

    @Immutable
    data class AllTime(val text: String) : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        val text: String,
        val period: Period.FromTo,
    ) : SelectedPeriodUi
}