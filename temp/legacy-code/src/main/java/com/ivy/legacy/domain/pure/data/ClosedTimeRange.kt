package com.ivy.wallet.domain.pure.data

import com.ivy.base.time.TimeProvider
import com.ivy.legacy.utils.beginningOfIvyTime
import com.ivy.legacy.utils.timeNowUTC
import java.time.Instant
import java.time.LocalDateTime

data class ClosedTimeRange(
    val from: Instant,
    val to: Instant,
) {
    companion object {
        fun allTimeIvy(
            timeProvider: TimeProvider,
        ): ClosedTimeRange = ClosedTimeRange(
            from = Instant.MIN,
            to = timeProvider.utcNow(),
        )

        fun to(to: Instant): ClosedTimeRange = ClosedTimeRange(
            from = Instant.MIN,
            to = to
        )
    }

    fun toFromToRange(): com.ivy.legacy.data.model.FromToTimeRange =
        com.ivy.legacy.data.model.FromToTimeRange(
            from = from,
            to = to
        )
}
