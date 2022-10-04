package com.ivy.common

import com.ivy.data.time.TimeRange
import java.time.LocalDateTime

fun TimeRange.fromToPair(): Pair<LocalDateTime, LocalDateTime> = Pair(from, to)