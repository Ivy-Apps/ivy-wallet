package com.ivy.wallet.functional.charts

import com.ivy.wallet.base.beginningOfIvyTime
import com.ivy.wallet.base.toEpochSeconds
import com.ivy.wallet.functional.data.ClosedTimeRange
import com.ivy.wallet.functional.wallet.calculateWalletBalance
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao
import java.math.BigDecimal
import java.time.LocalDateTime

data class ToRange(
    val to: LocalDateTime
)

data class ChartPoint(
    val range: ClosedTimeRange,
    val value: BigDecimal
)

suspend fun balanceChart(
    accountDao: AccountDao,
    transactionDao: TransactionDao,
    exchangeRateDao: ExchangeRateDao,
    baseCurrencyCode: String,
    period: ChartPeriod
): List<ChartPoint> {
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
    accumulator: List<ChartPoint> = emptyList()
): List<ChartPoint> {
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