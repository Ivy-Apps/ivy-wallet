package com.ivy.wallet.ui.charts

import com.ivy.wallet.ui.theme.components.charts.Value
import java.time.LocalDateTime

data class TimeValue(
    val dateTime: LocalDateTime,
    val value: Double
)

fun List<TimeValue>.toValues(): List<Value> {
    return this.mapIndexed { index, it ->
        Value(
            x = index.toDouble(),
            y = it.value
        )
    }
}