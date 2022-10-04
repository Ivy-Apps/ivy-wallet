package com.ivy.data.time

sealed interface SelectedPeriod {
    data class Monthly(
        val month: Month,
        val startDayOfMonth: Int,
        val range: TimeRange
    ) : SelectedPeriod

    data class InTheLast(
        val n: Int,
        val unit: TimeUnit,
        val range: TimeRange
    ) : SelectedPeriod

    data class AllTime(
        val range: TimeRange
    ) : SelectedPeriod

    data class CustomRange(
        val range: TimeRange
    ) : SelectedPeriod
}