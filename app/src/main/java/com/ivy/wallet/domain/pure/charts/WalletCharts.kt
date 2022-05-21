package com.ivy.wallet.domain.pure.charts

import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.domain.pure.data.WalletDAOs
import com.ivy.wallet.utils.beginningOfIvyTime
import com.ivy.wallet.utils.toEpochSeconds
import java.math.BigDecimal
import java.time.LocalDateTime

data class ToRange(
    val to: LocalDateTime
)

@Pure
suspend fun balanceChart(
    period: ChartPeriod,

    @SideEffect
    calcWalletBalance: suspend (ClosedTimeRange) -> BigDecimal
): List<SingleChartPoint> {
    val orderedPeriod = period.toRangesList().sortedBy {
        it.to.toEpochSeconds()
    }

    return generateBalanceChart(
        orderedPeriod = orderedPeriod.map { ToRange(it.to) },
        calcWalletBalance = calcWalletBalance
    )
}

@Pure
tailrec suspend fun generateBalanceChart(
    orderedPeriod: List<ToRange>,
    accumulator: List<SingleChartPoint> = emptyList(),

    @SideEffect
    calcWalletBalance: suspend (ClosedTimeRange) -> BigDecimal
): List<SingleChartPoint> {
    return if (orderedPeriod.isEmpty()) accumulator else {
        //recurse
        val toDateTime = orderedPeriod.first().to
        val previousChartPoint = accumulator.lastOrNull()

        val chartPoint = ChartPoint(
            range = ClosedTimeRange.to(to = toDateTime),
            value = calcWalletBalance(
                ClosedTimeRange(
                    from = previousChartPoint?.range?.to?.plusSeconds(1) ?: beginningOfIvyTime(),
                    to = toDateTime
                )
            ) + (previousChartPoint?.value ?: BigDecimal.ZERO)
        )

        generateBalanceChart(
            orderedPeriod = orderedPeriod.drop(1),
            calcWalletBalance = calcWalletBalance,
            accumulator = accumulator.plus(chartPoint)
        )
    }
}


suspend fun incomeExpenseChart(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    period: ChartPeriod
): List<IncomeExpenseChartPoint> {
    val orderedPeriod = period.toRangesList().sortedBy {
        it.to.toEpochSeconds()
    }

    return generateIncomeExpenseChart(
        orderedPeriod = orderedPeriod,
        calculateWalletIncomeExpense = { range ->
            TODO()
//            calculateWalletIncomeExpense(
//                walletDAOs = walletDAOs,
//                baseCurrencyCode = baseCurrencyCode,
//                range = range,
//                filterExcluded = true
//            ).value
        }
    )
}

tailrec suspend fun generateIncomeExpenseChart(
    orderedPeriod: List<ClosedTimeRange>,
    calculateWalletIncomeExpense: suspend (range: ClosedTimeRange) -> IncomeExpensePair,
    accumulator: List<IncomeExpenseChartPoint> = emptyList()
): List<IncomeExpenseChartPoint> {
    return if (orderedPeriod.isEmpty()) accumulator else {
        //recurse
        val range = orderedPeriod.first()

        val chartPoint = ChartPoint(
            range = range,
            value = calculateWalletIncomeExpense(range)
        )

        generateIncomeExpenseChart(
            orderedPeriod = orderedPeriod.drop(1),
            calculateWalletIncomeExpense = calculateWalletIncomeExpense,
            accumulator = accumulator.plus(chartPoint)
        )
    }
}


suspend fun incomeExpenseCountChart(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    period: ChartPeriod
): List<PairChartPoint> {
    val orderedPeriod = period.toRangesList().sortedBy {
        it.to.toEpochSeconds()
    }

    return generateIncomeExpenseCountChart(
        orderedPeriod = orderedPeriod,
        calculateWalletIncomeExpenseCount = { range ->
            TODO()
//            calculateWalletIncomeExpenseCount(
//                walletDAOs = walletDAOs,
//                baseCurrencyCode = baseCurrencyCode,
//                range = range,
//                filterExcluded = true
//            ).value
        }
    )
}

tailrec suspend fun generateIncomeExpenseCountChart(
    orderedPeriod: List<ClosedTimeRange>,
    calculateWalletIncomeExpenseCount: suspend (range: ClosedTimeRange) -> Pair<BigDecimal, BigDecimal>,
    accumulator: List<PairChartPoint> = emptyList()
): List<PairChartPoint> {
    return if (orderedPeriod.isEmpty()) accumulator else {
        //recurse
        val range = orderedPeriod.first()

        val chartPoint = ChartPoint(
            range = range,
            value = calculateWalletIncomeExpenseCount(range)
        )

        generateIncomeExpenseCountChart(
            orderedPeriod = orderedPeriod.drop(1),
            calculateWalletIncomeExpenseCount = calculateWalletIncomeExpenseCount,
            accumulator = accumulator.plus(chartPoint)
        )
    }
}