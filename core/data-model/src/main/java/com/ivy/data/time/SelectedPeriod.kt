package com.ivy.data.time

sealed interface SelectedPeriod {
    data class Monthly(
        val month: Month,
        val startDayOfMonth: Int,
        val period: Period.FromTo
    ) : SelectedPeriod

    data class InTheLast(
        val n: Int,
        val unit: TimeUnit,
        val period: Period.FromTo
    ) : SelectedPeriod

    data class AllTime(
        val period: Period.FromTo
    ) : SelectedPeriod

    data class CustomRange(
        val period: Period.FromTo
    ) : SelectedPeriod
}