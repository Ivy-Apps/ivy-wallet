package com.ivy.wallet.domain.fp.charts

import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.domain.fp.data.IncomeExpensePair
import java.math.BigDecimal

data class ChartPoint<V>(
    val range: ClosedTimeRange,
    val value: V
)

typealias SingleChartPoint = ChartPoint<BigDecimal>
typealias IncomeExpenseChartPoint = ChartPoint<IncomeExpensePair>
typealias PairChartPoint = ChartPoint<Pair<BigDecimal, BigDecimal>>