package com.ivy.wallet.functional.charts

import com.ivy.wallet.base.beginningOfIvyTime
import com.ivy.wallet.base.toEpochSeconds
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.wallet.IncomeExpense
import com.ivy.wallet.functional.wallet.calculateWalletBalance
import com.ivy.wallet.functional.wallet.calculateWalletIncomeExpense
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.time.LocalDateTime

data class ToRange(
    val to: LocalDateTime
)

data class ChartPoint<V>(
    val range: ClosedTimeRange,
    val value: V
)

typealias SingleChartPoint = ChartPoint<BigDecimal>
typealias IncomeExpenseChartPoint = ChartPoint<IncomeExpense>

suspend fun balanceChart(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    period: ChartPeriod
): List<SingleChartPoint> {
    val orderedPeriod = period.toRangesList().sortedBy {
        it.to.toEpochSeconds()
    }

    return generateBalanceChart(
        orderedPeriod = orderedPeriod.map { ToRange(it.to) },
        calculateWalletBalance = { range ->
            calculateWalletBalance(
                accountDao = accountDao,
                transactionDao = transactionDao,
                exchangeRateDao = exchangeRateDao,
                baseCurrencyCode = baseCurrencyCode,
                filterExcluded = true,
                range = range
            ).value
        }
    )
}

tailrec suspend fun generateBalanceChart(
    orderedPeriod: List<ToRange>,
    calculateWalletBalance: suspend (range: ClosedTimeRange) -> BigDecimal,
    accumulator: List<SingleChartPoint> = emptyList()
): List<SingleChartPoint> {
    return if (orderedPeriod.isEmpty()) accumulator else {
        //recurse
        val toDateTime = orderedPeriod.first().to
        val previousChartPoint = accumulator.lastOrNull()

        val chartPoint = ChartPoint(
            range = ClosedTimeRange.to(to = toDateTime),
            value = calculateWalletBalance(
                ClosedTimeRange(
                    from = previousChartPoint?.range?.to?.plusSeconds(1) ?: beginningOfIvyTime(),
                    to = toDateTime
                )
            ) + (previousChartPoint?.value ?: BigDecimal.ZERO)
        )

        generateBalanceChart(
            orderedPeriod = orderedPeriod.drop(1),
            calculateWalletBalance = calculateWalletBalance,
            accumulator = accumulator.plus(chartPoint)
        )
    }
}


suspend fun incomeExpenseChart(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    period: ChartPeriod
): List<IncomeExpenseChartPoint> {
    val orderedPeriod = period.toRangesList().sortedBy {
        it.to.toEpochSeconds()
    }

    return generateIncomeExpenseChart(
        orderedPeriod = orderedPeriod,
        calculateWalletIncomeExpense = { range ->
            calculateWalletIncomeExpense(
                accountDao = accountDao,
                transactionDao = transactionDao,
                exchangeRateDao = exchangeRateDao,
                baseCurrencyCode = baseCurrencyCode,
                range = range,
            ).value
        }
    )
}

tailrec suspend fun generateIncomeExpenseChart(
    orderedPeriod: List<ClosedTimeRange>,
    calculateWalletIncomeExpense: suspend (range: ClosedTimeRange) -> IncomeExpense,
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



