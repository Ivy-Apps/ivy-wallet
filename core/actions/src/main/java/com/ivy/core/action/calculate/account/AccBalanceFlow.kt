package com.ivy.core.action.calculate.account

import com.ivy.core.action.FlowAction
import com.ivy.core.functions.time.allTime
import com.ivy.data.account.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccBalanceFlow @Inject constructor(
    private val accStatsFlow: AccStatsFlow,
) : FlowAction<Account, Double>() {
    override suspend fun Account.createFlow(): Flow<Double> = accStatsFlow(
        AccStatsFlow.Input(
            account = this,
            period = allTime(),
            transfersAsIncomeExpense = false
        )
    ).map { it.balance }
}