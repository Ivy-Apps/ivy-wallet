package com.ivy.wallet.ui.charts

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import arrow.core.firstOrNone
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.ExchangeRate
import com.ivy.wallet.model.entity.Transaction
import java.time.LocalDateTime
import java.util.*

data class BalanceChartsValues(
    val balance: Double,
    val income: Double,
    val expense: Double,
    val incomeCount: Int,
    val expenseCount: Int
) {
    companion object {

        fun empty() = BalanceChartsValues(
            balance = 0.0,
            income = 0.0,
            expense = 0.0,
            incomeCount = 0,
            expenseCount = 0,
        )
    }
}

data class ToRange(
    val to: LocalDateTime
)

data class TransactionValue(
    val amount: Double,
    val type: TransactionType
)


fun calculateBalanceCharts(
    allTransactions: List<Transaction>,
    period: ChartPeriod,
    excludedAccounts: List<UUID>
): List<BalanceChartsValues> {
    val nonExcludedTransactions = allTransactions
        .filter {
            !excludedAccounts.contains(it.accountId)
        }

    val periodRanges = period.toRangesList()



    TODO()
}

suspend fun calculateBalanceCharts(
    toRange: ToRange,
    transactions: List<Transaction>,
    accumulator: BalanceChartsValues = BalanceChartsValues.empty()
): BalanceChartsValues {

    option {
        val transaction = transactions
            .firstOrNone().bind()
            .isInRange(toRange).bind()


        calculateBalanceCharts(
            toRange = toRange,
            transactions = transactions.drop(1),
            accumulator = accumulator.copy(
                balance = accumulator.balance + 0.0
            )
        )
    }


    return accumulator
}

private fun Transaction.isInRange(toRange: ToRange): Option<Transaction> =
    if (dateTime != null && dateTime.isBefore(toRange.to)) Some(this) else None

private fun Transaction.value(
    excludedAccounts: List<UUID>,
    baseCurrency: IvyCurrency,
    accountCurrency: Map<UUID, IvyCurrency>
): Option<TransactionValue> {
    if (excludedAccounts.contains(accountId)) {
        //excluded transaction

        if (type == TransactionType.TRANSFER &&
            toAccountId != null && !excludedAccounts.contains(toAccountId) &&
            toAmount != null
        ) {
            //transfer to not excluded account
            Some(
                TransactionValue(
                    amount = 0.0,
                    type = type
                )
            )

            toAmount
        } else {
            None
        }
    }

    TODO()
}

private fun Double.baseCurrencyAmount(
    trnCurrency: IvyCurrency,
    baseCurrency: IvyCurrency,
    exchangeRate: ExchangeRate
) {

}