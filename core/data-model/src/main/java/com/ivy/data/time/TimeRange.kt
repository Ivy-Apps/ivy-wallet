package com.ivy.data.time

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Deprecated("will be removed!")
@Immutable
data class TimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime,
)