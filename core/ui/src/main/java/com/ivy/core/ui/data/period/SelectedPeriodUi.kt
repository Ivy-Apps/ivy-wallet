package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(
        val month: MonthUi,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        val n: Int,
        val unit: TimeUnit,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class AllTime(
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi
}

fun SelectedPeriodUi.periodUi(): PeriodUi = when (this) {
    is SelectedPeriodUi.AllTime -> periodUi
    is SelectedPeriodUi.CustomRange -> periodUi
    is SelectedPeriodUi.InTheLast -> periodUi
    is SelectedPeriodUi.Monthly -> periodUi
}