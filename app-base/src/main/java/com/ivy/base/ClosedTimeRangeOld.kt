package com.ivy.base

import com.ivy.common.time.beginningOfIvyTime
import com.ivy.common.time.timeNow
import java.time.LocalDateTime

@Deprecated("old!")
data class ClosedTimeRangeOld(
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    companion object {
        fun allTimeIvy(): ClosedTimeRangeOld = ClosedTimeRangeOld(
            from = beginningOfIvyTime(),
            to = timeNow()
        )

        fun to(to: LocalDateTime): ClosedTimeRangeOld = ClosedTimeRangeOld(
            from = beginningOfIvyTime(),
            to = to
        )
    }

    fun toFromToRange(): FromToTimeRange = FromToTimeRange(
        from = from,
        to = to
    )
}