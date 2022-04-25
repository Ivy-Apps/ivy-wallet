package com.ivy.wallet.domain.pure.charts

import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import java.math.BigDecimal

data class ChartPoint<V>(
    val range: ClosedTimeRange,
    val value: V
)

typealias SingleChartPoint = ChartPoint<BigDecimal>
typealias IncomeExpenseChartPoint = ChartPoint<IncomeExpensePair>
typealias PairChartPoint = ChartPoint<Pair<BigDecimal, BigDecimal>>