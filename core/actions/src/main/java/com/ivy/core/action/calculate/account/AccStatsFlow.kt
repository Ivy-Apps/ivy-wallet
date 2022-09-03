package com.ivy.core.action.calculate.account

import arrow.core.nonEmptyListOf
import com.ivy.core.action.FlowAction
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.transaction.TrnsFlow
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.core.functions.transaction.and
import com.ivy.core.functions.transaction.chronological
import com.ivy.core.functions.transaction.foldTransactions
import com.ivy.core.functions.transaction.not
import com.ivy.data.account.Account
import com.ivy.data.time.Period
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccStatsFlow @Inject constructor(
    private val calculateAct: CalculateAct,
    private val trnsFlow: TrnsFlow,
) : FlowAction<AccStatsFlow.Input, Stats>() {
    data class Input(
        val account: Account,
        val period: Period,
        val transfersAsIncomeExpense: Boolean = false
    )

    override suspend fun Input.createFlow(): Flow<Stats> = combine(
        incomeExpenseStats(), transfersIn(), transfersOut()
    ) { stats, (tInAmount, tIn), (tOutAmount, tOut) ->
        Stats(
            balance = stats.balance + tInAmount - tOutAmount,
            income = if (transfersAsIncomeExpense) stats.income + tInAmount else stats.income,
            expense = if (transfersAsIncomeExpense) stats.expense + tOutAmount else stats.expense,
            incomesCount = if (transfersAsIncomeExpense)
                stats.incomesCount + tIn.size else stats.incomesCount,
            expensesCount = if (transfersAsIncomeExpense)
                stats.expensesCount + tOut.size else stats.expensesCount,
            trns = chronological(stats.trns + tIn + tOut)
        )
    }.flowOn(Dispatchers.Default)

    private suspend fun Input.incomeExpenseStats(): Flow<Stats> =
        trnsFlow(
            ByAccount(account) and ActualBetween(period) and not(ByType(TrnType.TRANSFER))
        ).map { trns ->
            calculateAct(
                CalculateAct.Input(
                    trns = trns,
                    outputCurrency = account.currency
                )
            )
        }.flowOn(Dispatchers.Default)

    private suspend fun Input.transfersIn(): Flow<Pair<Double, List<Transaction>>> =
        trnsFlow(
            ByType(TrnType.TRANSFER) and ActualBetween(period) and ByToAccount(account)
        ).map { transfersIn ->
            suspend fun transferInAmount(trn: Transaction, arg: Unit): Double =
                (trn.type as? TransactionType.Transfer)?.toValue?.amount ?: 0.0

            val amount = foldTransactions(
                transactions = transfersIn,
                valueFunctions = nonEmptyListOf(
                    ::transferInAmount
                ),
                arg = Unit
            ).head

            amount to transfersIn
        }.flowOn(Dispatchers.Default)

    private suspend fun Input.transfersOut(): Flow<Pair<Double, List<Transaction>>> =
        trnsFlow(
            ByAccount(account) and ActualBetween(period) and ByType(TrnType.TRANSFER)
        ).map { transfersOut ->
            suspend fun transferOutAmount(trn: Transaction, arg: Unit): Double = trn.value.amount

            val amount = foldTransactions(
                transactions = transfersOut,
                valueFunctions = nonEmptyListOf(
                    ::transferOutAmount
                ),
                arg = Unit
            ).head

            amount to transfersOut
        }.flowOn(Dispatchers.Default)

}