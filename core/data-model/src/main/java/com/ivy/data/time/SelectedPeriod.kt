package com.ivy.data.time

sealed interface SelectedPeriod {
    val range: TimeRange

    data class Monthly(
        val month: Month,
        val startDayOfMonth: Int,
        override val range: TimeRange
    ) : SelectedPeriod

    data class InTheLast(
        val n: Int,
        val unit: TimeUnit,
        override val range: TimeRange
    ) : SelectedPeriod

    data class AllTime(
        override val range: TimeRange
    ) : SelectedPeriod

    data class CustomRange(
        override val range: TimeRange
    ) : SelectedPeriod
}