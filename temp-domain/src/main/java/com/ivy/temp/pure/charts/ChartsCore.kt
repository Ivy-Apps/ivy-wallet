package com.ivy.wallet.domain.pure.charts

import java.math.BigDecimal

data class ChartPoint<V>(
    val range: com.ivy.base.ClosedTimeRange,
    val value: V
)

typealias SingleChartPoint = ChartPoint<BigDecimal>
typealias IncomeExpenseChartPoint = ChartPoint<IncomeExpensePair>
typealias PairChartPoint = ChartPoint<Pair<BigDecimal, BigDecimal>>