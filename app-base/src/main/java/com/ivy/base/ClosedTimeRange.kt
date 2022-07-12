package com.ivy.base

import com.ivy.common.beginningOfIvyTime
import com.ivy.common.timeNowUTC
import java.time.LocalDateTime

data class ClosedTimeRange(
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    companion object {
        fun allTimeIvy(): ClosedTimeRange = ClosedTimeRange(
            from = beginningOfIvyTime(),
            to = timeNowUTC()
        )

        fun to(to: LocalDateTime): ClosedTimeRange = ClosedTimeRange(
            from = beginningOfIvyTime(),
            to = to
        )
    }

    fun toFromToRange(): FromToTimeRange = FromToTimeRange(
        from = from,
        to = to
    )
}