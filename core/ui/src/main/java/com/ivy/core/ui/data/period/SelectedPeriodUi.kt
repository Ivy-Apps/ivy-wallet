package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(
        val btnText: String,
        val month: MonthUi,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        val btnText: String,
        val n: Int,
        val unit: TimeUnit,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class AllTime(
        val btnText: String,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        val btnText: String,
        val periodUi: PeriodUi,
    ) : SelectedPeriodUi
}

fun SelectedPeriodUi.periodUi(): PeriodUi = when (this) {
    is SelectedPeriodUi.AllTime -> periodUi
    is SelectedPeriodUi.CustomRange -> periodUi
    is SelectedPeriodUi.InTheLast -> periodUi
    is SelectedPeriodUi.Monthly -> periodUi
}

fun SelectedPeriodUi.btnText(): String = when (this) {
    is SelectedPeriodUi.AllTime -> btnText
    is SelectedPeriodUi.CustomRange -> btnText
    is SelectedPeriodUi.InTheLast -> btnText
    is SelectedPeriodUi.Monthly -> btnText
}