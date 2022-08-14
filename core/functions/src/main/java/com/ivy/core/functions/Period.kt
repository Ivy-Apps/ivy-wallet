package com.ivy.core.functions

import com.ivy.common.beginningOfIvyTime
import com.ivy.common.endOfIvyTime
import com.ivy.data.Period
import java.time.LocalDateTime

fun allTime(): Period = Period.FromTo(
    from = beginningOfIvyTime(),
    to = endOfIvyTime()
)

fun Period.toRange(): Pair<LocalDateTime, LocalDateTime> = when (this) {
    is Period.After -> Pair(from, endOfIvyTime())
    is Period.Before -> Pair(beginningOfIvyTime(), to)
    is Period.FromTo -> Pair(from, to)
}
