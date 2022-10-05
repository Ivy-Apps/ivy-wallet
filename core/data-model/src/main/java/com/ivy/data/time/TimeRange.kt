package com.ivy.data.time

import java.time.LocalDateTime

data class TimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime,
)