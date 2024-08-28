package com.ivy.legacy.data.model

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.Transaction
import com.ivy.base.time.TimeProvider
import com.ivy.legacy.utils.beginningOfIvyTime
import com.ivy.legacy.utils.convertLocalToUTC
import com.ivy.legacy.utils.dateNowUTC
import com.ivy.legacy.utils.toIvyFutureTime
import com.ivy.ui.time.TimeFormatter
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import java.time.Instant
import java.time.ZoneOffset

@Immutable
data class FromToTimeRange(
    val from: Instant?,
    val to: Instant?,
) {
    fun from(): Instant =
        from ?: Instant.MIN

    fun to(): Instant =
        to ?: Instant.MAX

    fun upcomingFrom(
        timeProvider: TimeProvider
    ): Instant {
        val startOfDayNowUTC = timeProvider.utcNow()
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else from()
    }

    fun overdueTo(
        timeProvider: TimeProvider
    ): Instant {
        val startOfDayNowUTC = timeProvider.utcNow()
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else to()
    }

    fun includes(dateTime: Instant): Boolean =
        dateTime.isAfter(from()) && dateTime.isBefore(to())

    fun toDisplay(
        timeFormatter: TimeFormatter
    ): String = with(timeFormatter) {
        val style = TimeFormatter.Style.DateOnly(includeWeekDay = false)
        when {
            from != null && to != null -> {
                "${from.formatLocal(style)} - ${to.formatLocal(style)}"
            }

            from != null && to == null -> {
                "From ${from.formatLocal(style)}"
            }

            from == null && to != null -> {
                "To ${to.formatLocal(style)}"
            }

            else -> {
                "Range"
            }
        }
    }
}

@Deprecated("Uses legacy Transaction")
fun Iterable<Transaction>.filterUpcomingLegacy(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        // make sure that it's in the future
        it.dueDate != null && it.dueDate!!.isAfter(todayStartOfDayUTC)
    }
}

fun Iterable<com.ivy.data.model.Transaction>.filterUpcoming(): List<com.ivy.data.model.Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay().toInstant(ZoneOffset.UTC)

    return filter {
        // make sure that it's in the future
        !it.settled && it.time.isAfter(todayStartOfDayUTC)
    }
}

@Deprecated("Uses legacy Transaction")
fun Iterable<Transaction>.filterOverdueLegacy(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        // make sure that it's in the past
        it.dueDate != null && it.dueDate!!.isBefore(todayStartOfDayUTC)
    }
}

fun Iterable<com.ivy.data.model.Transaction>.filterOverdue(): List<com.ivy.data.model.Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay().toInstant(ZoneOffset.UTC)

    return filter {
        // make sure that it's in the past
        !it.settled && it.time.isBefore(todayStartOfDayUTC)
    }
}

fun FromToTimeRange.toCloseTimeRangeUnsafe(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from(),
        to = to()
    )
}

fun FromToTimeRange.toCloseTimeRange(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from ?: beginningOfIvyTime(),
        to = to ?: toIvyFutureTime()
    )
}

fun FromToTimeRange.toUTCCloseTimeRange(): ClosedTimeRange {
    return ClosedTimeRange(
        from = from ?: beginningOfIvyTime(),
        to = to ?: toIvyFutureTime()
    )
}
