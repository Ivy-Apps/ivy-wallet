package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.IncomeExpensePair
import com.ivy.wallet.domain.logic.WalletLogic
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import javax.inject.Inject

class CalcOverdueAct @Inject constructor(
    private val walletLogic: WalletLogic
) : Action<FromToTimeRange, CalcOverdueAct.Output>() {

    override suspend fun FromToTimeRange.willDo(): Output = io {
        //TODO: Rework & optimize this
        Output(
            overdue = IncomeExpensePair(
                income = walletLogic.calculateOverdueIncome(this).toBigDecimal(),
                expense = walletLogic.calculateOverdueExpenses(this).toBigDecimal()
            ),
            overdueTrns = walletLogic.overdueTransactions(this)
        )
    }

    data class Output(
        val overdue: IncomeExpensePair,
        val overdueTrns: List<Transaction>
    )
}