package com.ivy.wallet.ui.charts

import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.theme.components.charts.Value

data class TimeValue(
    val range: FromToTimeRange,
    val period: ChartPeriod,
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