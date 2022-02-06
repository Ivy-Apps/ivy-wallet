package com.ivy.wallet.functional.data

import com.ivy.wallet.base.beginningOfIvyTime
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
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