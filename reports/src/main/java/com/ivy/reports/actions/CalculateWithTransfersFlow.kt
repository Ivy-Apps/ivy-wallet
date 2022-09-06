package com.ivy.reports.actions

import arrow.core.nonEmptyListOf
import com.ivy.core.action.FlowAction
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.currency.exchange.ExchangeRatesFlow
import com.ivy.core.functions.exchange.exchange
import com.ivy.core.functions.transaction.foldTransactions
import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRates
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Calculates [ExtendedStats] (income, expense, counts, balance) for a list of [Transaction]
 * converted in a **outputCurrency** of your choice.
 */
class CalculateWithTransfersFlow @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowAction<CalculateWithTransfersFlow.Input, ExtendedStats>() {
    data class Input(
        val trns: List<Transaction>,
        val outputCurrency: CurrencyCode,
        val accounts: List<Account>
    )

    override fun Input.createFlow(): Flow<ExtendedStats> =
        combine(baseCurrencyFlow(), exchangeRatesFlow()) { baseCurr, exchangeRates ->
            calculate(baseCurrency = baseCurr, exchangeRates = exchangeRates)
        }

    private suspend fun Input.calculate(
        baseCurrency: CurrencyCode,
        exchangeRates: ExchangeRates
    ): ExtendedStats {
        val res = foldTransactions(
            transactions = trns,
            valueFunctions = nonEmptyListOf(
                ::income,
                ::expense,
                ::incomeCount,
                ::expenseCount,
                ::transfersInCount,
                ::transfersOutCount,
                ::transfersInAmount,
                ::transfersOutAmount
            ),
            arg = FoldArg(
                rates = exchangeRates,
                baseCurrency = baseCurrency,
                outputCurrency = this.outputCurrency,
                accounts = this.accounts.toHashSet()
            )
        )

        val income = res[0]
        val expense = res[1]
        val tIn = res[6]
        val tOut = res[7]

        return ExtendedStats(
            balance = income - expense + tIn - tOut,
            income = income,
            expense = expense,
            incomesCount = res[2].toInt(),
            expensesCount = res[3].toInt(),
            transfersInCount = res[4].toInt(),
            transfersOutCount = res[5].toInt(),
            transfersInAmount = tIn,
            transfersOutAmount = tOut,
            trns = trns
        )
    }

    private suspend fun income(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Income -> trnAmountInCurrency(trn, arg)
        else -> 0.0
    }

    private suspend fun expense(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Expense -> trnAmountInCurrency(trn, arg)
        else -> 0.0
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun incomeCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Income -> 1.0
        else -> 0.0
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun expenseCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Expense -> 1.0
        else -> 0.0
    }

    private suspend fun transfersInCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.accounts
            val typeTransfer = trn.type as TransactionType.Transfer

            if (availableAccounts.contains(typeTransfer.toAccount))
                1.0
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun transfersOutCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.accounts

            if (availableAccounts.contains(trn.account))
                1.0
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun transfersInAmount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.accounts
            val typeTransfer = trn.type as TransactionType.Transfer

            if (availableAccounts.contains(typeTransfer.toAccount))
                trnAmountInCurrency(
                    trn = trn.copy(value = typeTransfer.toValue),
                    arg = arg
                )
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun transfersOutAmount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.accounts

            if (availableAccounts.contains(trn.account))
                trnAmountInCurrency(trn, arg = arg)
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun trnAmountInCurrency(
        trn: Transaction,
        arg: FoldArg
    ): Double =
        exchange(
            rates = arg.rates,
            baseCurrency = arg.outputCurrency,
            from = arg.outputCurrency,
            to = arg.outputCurrency,
            amount = trn.value.amount
        ).orNull() ?: 0.0

    private data class FoldArg(
        val rates: ExchangeRates,
        val baseCurrency: CurrencyCode,
        val outputCurrency: CurrencyCode,
        val accounts: HashSet<Account>,
    )

}

data class ExtendedStats(
    val balance: Double,
    val income: Double,
    val expense: Double,
    val incomesCount: Int,
    val expensesCount: Int,
    val transfersInCount: Int,
    val transfersOutCount: Int,
    val transfersInAmount: Double,
    val transfersOutAmount: Double,
    val trns: List<Transaction>
) {
    companion object {
        fun empty() = ExtendedStats(
            balance = .0,
            income = 0.0,
            expense = .0,
            incomesCount = 0,
            expensesCount = 0,
            transfersInCount = 0,
            transfersOutCount = 0,
            transfersInAmount = 0.0,
            transfersOutAmount = 0.0,
            trns = emptyList()
        )
    }
}