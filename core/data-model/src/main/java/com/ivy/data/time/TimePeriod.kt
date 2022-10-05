package com.ivy.data.time

sealed interface TimePeriod {
    data class Dynamic(val dynamic: DynamicTimePeriod) : TimePeriod
    data class Fixed(val range: TimeRange) : TimePeriod
}