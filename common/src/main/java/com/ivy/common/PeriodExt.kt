package com.ivy.common

import com.ivy.data.time.Period
import java.time.LocalDateTime

fun Period.toRange(): Pair<LocalDateTime, LocalDateTime> = when (this) {
    is Period.After -> Pair(from, endOfIvyTime())
    is Period.Before -> Pair(beginningOfIvyTime(), to)
    is Period.FromTo -> Pair(from, to)
}