package com.ivy.core.action.calculate.account

import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.calculate.StatsAct
import com.ivy.core.action.transaction.read.TrnsAct
import com.ivy.core.functions.allTime
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class AccStatsAct @Inject constructor(
    private val statsAct: StatsAct,
    private val transfersInAct: TransfersInAct,
    private val transfersOutAct: TransfersOutAct,
    private val transactionDao: TransactionDao,
    private val trnsAct: TrnsAct
) : FPAction<AccStatsAct.Input, Stats>() {
    data class Input(
        val account: Account,
        val period: Period,
        val transfersAsIncomeExpense: Boolean = false
    )

    override suspend fun Input.compose(): suspend () -> Stats = {
        val stats = nonTransferStats()
        val tIn = transfersInAct(account)
        val tOut = transfersOutAct(account)

        Stats(
            balance = stats.balance + tIn.amount - tOut.amount,
            income = if (transfersAsIncomeExpense) stats.income + tIn.amount else stats.income,
            expense = if (transfersAsIncomeExpense) stats.expense + tOut.amount else stats.expense,
            incomesCount = if (transfersAsIncomeExpense)
                stats.incomesCount + tIn.transfersIn.size else stats.incomesCount,
            expensesCount = if (transfersAsIncomeExpense)
                stats.expensesCount + tOut.transfersOut.size else stats.expensesCount
        )
    }

    private suspend fun Input.nonTransferStats(): Stats = TrnsAct.Input(
        period = allTime(),
        query = { from, to ->
            transactionDao.findAllByAccountAndBetween(
                accountId = account.id,
                startDate = from,
                endDate = to
            )
        }
    ) asParamTo trnsAct then { trns ->
        StatsAct.Input(
            trns = trns,
            outputCurrency = account.currency
        )
    } thenInvokeAfter statsAct
}