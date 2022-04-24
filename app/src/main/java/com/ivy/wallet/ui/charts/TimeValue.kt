package com.ivy.wallet.ui.charts

import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.domain.pure.charts.SingleChartPoint
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.ui.theme.components.charts.linechart.Value

data class TimeValue(
    val range: FromToTimeRange,
    val period: ChartPeriod,
    val value: Double
)

fun List<SingleChartPoint>.toValues2(): List<Value> {
    return this.mapIndexed { index, it ->
        Value(
            x = index.toDouble(),
            y = it.value.toDouble()
        )
    }
}

fun List<TimeValue>.toValue(): List<Value> {
    return this.mapIndexed { index, it ->
        Value(
            x = index.toDouble(),
            y = it.value
        )
    }
}