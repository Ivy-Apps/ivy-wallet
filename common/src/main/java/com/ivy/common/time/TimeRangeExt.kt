package com.ivy.common.time

import com.ivy.data.time.TimeRange
import java.time.LocalDateTime

fun TimeRange.toPair(): Pair<LocalDateTime, LocalDateTime> = from to to