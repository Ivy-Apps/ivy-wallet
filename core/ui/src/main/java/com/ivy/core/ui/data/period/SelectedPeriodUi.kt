package com.ivy.core.ui.data.period

import androidx.compose.runtime.Immutable
import com.ivy.data.time.TimeUnit

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(
        val btnText: String,
        val month: MonthUi,
        val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class InTheLast(
        val btnText: String,
        val n: Int,
        val unit: TimeUnit,
        val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class AllTime(
        val btnText: String,
        val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi

    @Immutable
    data class CustomRange(
        val btnText: String,
        val rangeUi: TimeRangeUi,
    ) : SelectedPeriodUi
}

fun SelectedPeriodUi.rangeUi(): TimeRangeUi = when (this) {
    is SelectedPeriodUi.AllTime -> rangeUi
    is SelectedPeriodUi.CustomRange -> rangeUi
    is SelectedPeriodUi.InTheLast -> rangeUi
    is SelectedPeriodUi.Monthly -> rangeUi
}

fun SelectedPeriodUi.btnText(): String = when (this) {
    is SelectedPeriodUi.AllTime -> btnText
    is SelectedPeriodUi.CustomRange -> btnText
    is SelectedPeriodUi.InTheLast -> btnText
    is SelectedPeriodUi.Monthly -> btnText
}