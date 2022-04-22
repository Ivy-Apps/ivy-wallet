package com.ivy.wallet.domain.pure.data

import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.utils.beginningOfIvyTime
import com.ivy.wallet.utils.timeNowUTC
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