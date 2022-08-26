@file:Suppress("RedundantSuspendModifier")

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
        val selectedAccounts: List<Account>
    )

    override suspend fun Input.compose(): suspend () -> ExtendedStats = {
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
            arg = Pair(outputCurrency, selectedAccounts.toHashSet())
        )

        val income = res[0]
        val expense = res[1]
        val tIn = res[6]
        val tOut = res[7]

        ExtendedStats(
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

    @Suppress("UNUSED_PARAMETER")
    private suspend fun incomeCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Income -> 1.0
        else -> 0.0
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun expenseCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        TransactionType.Expense -> 1.0
        else -> 0.0
    }

    private suspend fun transfersInCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.second
            val typeTransfer = trn.type as TransactionType.Transfer

            if (availableAccounts.contains(typeTransfer.toAccount))
                1.0
            else
                0.0
        }
        else -> 0.0
    }

    private suspend fun transfersOutCount(
        trn: Transaction, arg: Pair<CurrencyCode, HashSet<Account>>
    ): Double = when (trn.type) {
        is TransactionType.Transfer -> {
            val availableAccounts = arg.second

            if (availableAccounts.contains(trn.account))
                1.0
            else
                0.0
        }
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