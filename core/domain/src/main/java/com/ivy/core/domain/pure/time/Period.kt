package com.ivy.core.domain.pure.time

import com.ivy.common.beginningOfIvyTime
import com.ivy.common.endOfIvyTime
import com.ivy.data.time.Period

fun allTime(): Period = Period.FromTo(
    from = beginningOfIvyTime(),
    to = endOfIvyTime()
)
