package com.ivy.core.action.calculate.account

import arrow.core.nonEmptyListOf
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.transaction.TrnsAct
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.core.functions.transaction.and
import com.ivy.core.functions.transaction.chronological
import com.ivy.core.functions.transaction.foldTransactions
import com.ivy.core.functions.transaction.not
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnType
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import javax.inject.Inject

class AccActualStatsAct @Inject constructor(
    private val calculateAct: CalculateAct,
    private val trnsAct: TrnsAct
) : FPAction<AccActualStatsAct.Input, Stats>() {
    data class Input(
        val account: Account,
        val period: Period,
        val transfersAsIncomeExpense: Boolean = false
    )

    override suspend fun Input.compose(): suspend () -> Stats = {
        val stats = nonTransferStats()
        val (tInAmount, tIn) = transfersInStats()
        val (tOutAmount, tOut) = transfersOutStats()

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
    }

    private suspend fun Input.transfersInStats(): Pair<Double, List<Transaction>> = {
        ByType(TrnType.TRANSFER) and ActualBetween(period) and ByToAccount(account)
    } then trnsAct thenInvokeAfter { transfersIn ->
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
    }

    private suspend fun Input.transfersOutStats(): Pair<Double, List<Transaction>> = {
        ByAccount(account) and ActualBetween(period) and ByType(TrnType.TRANSFER)
    } then trnsAct thenInvokeAfter { transfersOut ->
        suspend fun transferOutAmount(trn: Transaction, arg: Unit): Double = trn.value.amount

        val amount = foldTransactions(
            transactions = transfersOut,
            valueFunctions = nonEmptyListOf(
                ::transferOutAmount
            ),
            arg = Unit
        ).head

        amount to transfersOut
    }

    private suspend fun Input.nonTransferStats(): Stats = {
        ByAccount(account) and ActualBetween(period) and not(ByType(TrnType.TRANSFER))
    } then trnsAct then { trns ->
        CalculateAct.Input(
            trns = trns,
            outputCurrency = account.currency
        )
    } thenInvokeAfter calculateAct
}