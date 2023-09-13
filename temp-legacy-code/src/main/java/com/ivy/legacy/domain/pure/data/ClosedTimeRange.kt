package com.ivy.wallet.domain.pure.data

import com.ivy.legacy.utils.beginningOfIvyTime
import com.ivy.legacy.utils.timeNowUTC
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

    fun toFromToRange(): com.ivy.legacy.data.model.FromToTimeRange =
        com.ivy.legacy.data.model.FromToTimeRange(
            from = from,
            to = to
        )
}
