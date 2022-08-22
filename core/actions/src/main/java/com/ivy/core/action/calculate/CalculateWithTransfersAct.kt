package com.ivy.core.action.calculate

import arrow.core.nonEmptyListOf
import com.ivy.core.action.currency.exchange.ExchangeAct
import com.ivy.core.functions.transaction.foldTransactions
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.thenInvokeAfter
import javax.inject.Inject

/**
 * Calculates [ExtendedStats] (income, expense, counts, balance) for a list of [Transaction]
 * converted in a **outputCurrency** of your choice.
 */
class CalculateWithTransfersAct @Inject constructor(
    private val exchangeAct: ExchangeAct
) : FPAction<CalculateWithTransfersAct.Input, ExtendedStats>() {
    data class Input(
        val trns: List<Transaction>,
        val outputCurrency: CurrencyCode,
        val transfersAsIncomeExpense: Boolean = false
    )

    override suspend fun Input.compose(): suspend () -> ExtendedStats = {
        val availableAccounts = trns.map { it.account }.toHashSet()
        val res = foldTransactions(
            transactions = trns,
            valueFunctions = nonEmptyListOf(
                ::income,
                ::expense,
                ::incomeCount,
                ::expenseCount,
                ::transfersCount,
                ::transfersInAmount,
                ::transfersOutAmount
            ),
            arg = Pair(outputCurrency, availableAccounts)
        )

        val income = res[0]
        val expense = res[1]
        val tIn = res[5]
        val tOut = res[6]

        ExtendedStats(
            balance = income - expense + tIn - tOut,
            income = income + if (transfersAsIncomeExpense) tIn else 0.0,
            expense = expense + if (transfersAsIncomeExpense) tOut else 0.0,
            incomesCount = res[2].toInt() + if (transfersAsIncomeExpense) res[4].toInt() else 0,
            expensesCount = res[3].toInt() + if (transfersAsIncomeExpense) res[4].toInt() else 0,
            transfersInAmount = tIn,
            transfersOutAmount = tOut,
            trns = trns
        )
    }

    private suspend fun income(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Income -> trnAmountInCurrency(trn, arg.first)
        else -> 0.0
    }

    private suspend fun expense(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Expense -> trnAmountInCurrency(trn, arg.first)
        else -> 0.0
    }

    private suspend fun incomeCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Income -> 1.0
        else -> 0.0
    }

    private suspend fun expenseCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Expense -> 1.0
        else -> 0.0
    }

    private suspend fun transfersCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> 1.0
        else -> 0.0
    }

    private suspend fun transfersInAmount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.second
            val typeTransfer = trn.type as TransactionType.Transfer

            if (availableAccounts.contains(typeTransfer.toAccount))
                trnAmountInCurrency(
                    trn.copy(value = typeTransfer.toValue),
                    outputCurrency = arg.first
                )
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun transfersOutAmount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.second

            if (availableAccounts.contains(trn.account))
                trnAmountInCurrency(trn, outputCurrency = arg.first)
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun trnAmountInCurrency(
        trn: Transaction,
        outputCurrency: CurrencyCode
    ): Double =
        ExchangeAct.Input(
            from = trn.value.currency,
            to = outputCurrency,
            amount = trn.value.amount
        ) asParamTo exchangeAct thenInvokeAfter {
            it.orNull() ?: 0.0
        }
}

data class ExtendedStats(
    val balance: Double,
    val income: Double,
    val expense: Double,
    val incomesCount: Int,
    val expensesCount: Int,
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
            transfersInAmount = 0.0,
            transfersOutAmount = 0.0,
            trns = emptyList()
        )
    }
}