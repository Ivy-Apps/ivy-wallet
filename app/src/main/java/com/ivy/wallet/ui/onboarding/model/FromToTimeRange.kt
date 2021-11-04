package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.formatDateOnly
import com.ivy.wallet.base.startOfDayNowUTC
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.model.entity.Transaction
import java.time.LocalDateTime

data class FromToTimeRange(
    val from: LocalDateTime?,
    val to: LocalDateTime?,
) {
    fun from(): LocalDateTime =
        from ?: timeNowUTC().minusYears(30)

    fun to(): LocalDateTime =
        to ?: timeNowUTC().plusYears(30)

    fun upcomingFrom(): LocalDateTime {
        val startOfDayNowUTC =
            startOfDayNowUTC().minusDays(1) //-1 day to ensure that everything is included
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else from()
    }

    fun overdueTo(): LocalDateTime {
        val startOfDayNowUTC =
            startOfDayNowUTC().plusDays(1) //+1 day to ensure that everything is included
        return if (includes(startOfDayNowUTC)) startOfDayNowUTC else to()
    }

    fun includes(dateTime: LocalDateTime): Boolean =
        dateTime.isAfter(from()) && dateTime.isBefore(to())

    fun toDisplay(): String {
        return when {
            from != null && to != null -> {
                "${from.toLocalDate().formatDateOnly()} - ${to.toLocalDate().formatDateOnly()}"
            }
            from != null && to == null -> {
                "From ${from.toLocalDate().formatDateOnly()}"
            }
            from == null && to != null -> {
                "To ${to.toLocalDate().formatDateOnly()}"
            }
            else -> {
                "Range"
            }
        }
    }
}

fun Iterable<Transaction>.filterUpcoming(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        //make sure that it's in the future
        it.dueDate != null && it.dueDate.isAfter(todayStartOfDayUTC)
    }
}

fun Iterable<Transaction>.filterOverdue(): List<Transaction> {
    val todayStartOfDayUTC = dateNowUTC().atStartOfDay()

    return filter {
        //make sure that it's in the past
        it.dueDate != null && it.dueDate.isBefore(todayStartOfDayUTC)
    }
}