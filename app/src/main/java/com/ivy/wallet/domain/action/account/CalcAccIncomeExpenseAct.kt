package com.ivy.wallet.domain.action.account

import arrow.core.nonEmptyListOf
import com.ivy.fp.action.FPAction
import com.ivy.fp.action.then
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.domain.pure.transaction.AccountValueFunctions
import com.ivy.wallet.domain.pure.transaction.foldTransactions
import javax.inject.Inject

class CalcAccIncomeExpenseAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct
) : FPAction<CalcAccIncomeExpenseAct.Input, CalcAccIncomeExpenseAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id,
            range = range
        )
    } then accTrnsAct then { accTrns ->
        foldTransactions(
            transactions = accTrns,
            arg = account.id,
            valueFunctions = nonEmptyListOf(
                AccountValueFunctions::income,
                AccountValueFunctions::expense
            )
        )
    } then { values ->
        Output(
            account = account,
            incomeExpensePair = IncomeExpensePair(
                income = values[0],
                expense = values[1]
            )
        )
    }

    data class Input(
        val account: Account,
        val range: ClosedTimeRange = ClosedTimeRange.allTimeIvy()
    )

    data class Output(
        val account: Account,
        val incomeExpensePair: IncomeExpensePair
    )
}