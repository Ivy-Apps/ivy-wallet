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

    object AllTime : SelectedPeriod()
    object CustomRange : SelectedPeriod()
}