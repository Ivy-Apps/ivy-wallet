package com.ivy.base

import com.ivy.common.time.formatDateOnly
import com.ivy.common.time.timeNow
import java.time.LocalDateTime

data class FromToTimeRange(
    val from: LocalDateTime?,
    val to: LocalDateTime?,
) {
    fun from(): LocalDateTime =
        from ?: timeNow().minusYears(30)

    fun to(): LocalDateTime =
        to ?: timeNow().plusYears(30)

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
