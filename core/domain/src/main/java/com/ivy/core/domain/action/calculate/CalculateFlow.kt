package com.ivy.core.domain.action.calculate

import arrow.core.nonEmptyListOf
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.currency.exchange.ExchangeRatesFlow
import com.ivy.core.domain.functions.exchange.exchange
import com.ivy.core.domain.functions.transaction.foldTransactions
import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Calculates [Stats] (income, expense, counts, balance) for a list of [Transaction]
 * converted in a **outputCurrency** of your choice.
 */
class CalculateFlow @Inject constructor(
    private val exchangeRatesFlow: ExchangeRatesFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : com.ivy.core.domain.action.FlowAction<CalculateFlow.Input, Stats>() {
    data class Input(
        val trns: List<Transaction>,
        val outputCurrency: CurrencyCode
    )

    override fun Input.createFlow(): Flow<Stats> =
        combine(exchangeRatesFlow(), baseCurrencyFlow()) { rates, baseCurrency ->
            calculate(rates = rates, baseCurrency = baseCurrency)
        }

    suspend fun Input.calculate(
        rates: ExchangeRatesMap,
        baseCurrency: CurrencyCode,
    ): Stats {
        val res = foldTransactions(
            transactions = trns,
            valueFunctions = nonEmptyListOf(
                ::income,
                ::expense,
                ::incomeCount,
                ::expenseCount,
            ),
            arg = FoldArg(
                rates = rates,
                baseCurrency = baseCurrency,
                outputCurrency = outputCurrency,
            )
        )

        val income = res[0]
        val expense = res[1]

        return Stats(
            balance = income - expense,
            income = income,
            expense = expense,
            incomesCount = res[2].toInt(),
            expensesCount = res[3].toInt(),
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

    private suspend fun incomeCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Income -> 1.0
        else -> 0.0
    }

    private suspend fun expenseCount(
        trn: Transaction, arg: FoldArg
    ): Double = when (trn.type) {
        TransactionType.Expense -> 1.0
        else -> 0.0
    }

    private suspend fun trnAmountInCurrency(
        trn: Transaction,
        arg: FoldArg
    ): Double = exchange(
        rates = arg.rates,
        baseCurrency = arg.baseCurrency,
        from = trn.value.currency,
        to = arg.outputCurrency,
        amount = trn.value.amount,
    ).orNull() ?: 0.0

    private data class FoldArg(
        val rates: ExchangeRatesMap,
        val baseCurrency: CurrencyCode,
        val outputCurrency: CurrencyCode,
    )
}