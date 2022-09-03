package com.ivy.data.time

sealed class SelectedPeriod {
    data class Monthly(
        val month: Month,
        val period: Period.FromTo
    ) : SelectedPeriod()

    data class InTheLast(
        val n: Int,
        val unit: TimeUnit,
        val period: Period.FromTo
    ) : SelectedPeriod()

    data class AllTime(
        val period: Period.FromTo
    ) : SelectedPeriod()

    data class CustomRange(
        val period: Period.FromTo
    ) : SelectedPeriod()
}