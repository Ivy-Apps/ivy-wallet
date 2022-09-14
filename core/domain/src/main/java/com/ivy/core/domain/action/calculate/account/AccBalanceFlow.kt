package com.ivy.core.domain.action.calculate.account

import com.ivy.core.domain.functions.time.allTime
import com.ivy.data.account.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccBalanceFlow @Inject constructor(
    private val accStatsFlow: AccStatsFlow,
) : com.ivy.core.domain.action.FlowAction<Account, Double>() {
    override fun Account.createFlow(): Flow<Double> = accStatsFlow(
        AccStatsFlow.Input(
            account = this,
            period = allTime(),
            transfersAsIncomeExpense = false
        )
    ).map { it.balance }
}