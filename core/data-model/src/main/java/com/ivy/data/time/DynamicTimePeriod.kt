package com.ivy.data.time

sealed interface DynamicTimePeriod {
    /**
     * this week, this month, this year...
     * @param offset used to offset next or last week/month/etc
     */
    data class Calendar(
        val unit: TimeUnit,
        val offset: Int = 0
    ) : DynamicTimePeriod

    /**
     * last n days/weeks/months/year
     */
    data class Last(val n: Int, val unit: TimeUnit) : DynamicTimePeriod

    /**
     * next n days/weeks/months/year
     */
    data class Next(val n: Int, val unit: TimeUnit) : DynamicTimePeriod
}