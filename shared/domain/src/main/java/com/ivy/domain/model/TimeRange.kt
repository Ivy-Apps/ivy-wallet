package com.ivy.domain.model

import java.time.Instant

sealed interface TimeRange {
    data object AllTime : TimeRange

    /**
     * @param time before this point in time (inclusive)
     */
    data class Before(val time: Instant) : TimeRange

    /**
     * @param time after this point in time (inclusive)
     */
    data class After(val time: Instant) : TimeRange
}