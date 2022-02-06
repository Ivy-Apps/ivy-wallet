package com.ivy.wallet.functional.charts

import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.wallet.IncomeExpense
import java.math.BigDecimal

data class ChartPoint<V>(
    val range: ClosedTimeRange,
    val value: V
)

typealias SingleChartPoint = ChartPoint<BigDecimal>
typealias IncomeExpenseChartPoint = ChartPoint<IncomeExpense>
typealias PairChartPoint = ChartPoint<Pair<BigDecimal, BigDecimal>>