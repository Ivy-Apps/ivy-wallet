package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.IncomeExpensePair
import com.ivy.wallet.domain.logic.WalletLogic
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import javax.inject.Inject

class CalcUpcomingAct @Inject constructor(
    private val walletLogic: WalletLogic
) : Action<FromToTimeRange, CalcUpcomingAct.Output>() {

    override suspend fun FromToTimeRange.willDo(): Output = io {
        //TODO: Rework & optimize this
        Output(
            upcoming = IncomeExpensePair(
                income = walletLogic.calculateUpcomingIncome(this).toBigDecimal(),
                expense = walletLogic.calculateUpcomingExpenses(this).toBigDecimal()
            ),
            upcomingTrns = walletLogic.upcomingTransactions(this)
        )
    }

    data class Output(
        val upcoming: IncomeExpensePair,
        val upcomingTrns: List<Transaction>
    )
}