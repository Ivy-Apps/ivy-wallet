package com.ivy.core.action.calculate.wallet

import com.ivy.core.action.FlowAction
import com.ivy.core.action.account.AccountsAct
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.calculate.account.AccBalanceAct
import com.ivy.core.action.calculate.account.AccBalanceFlow
import com.ivy.frp.action.thenMap
import com.ivy.frp.lambda
import com.ivy.frp.then
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TotalBalanceFlow @Inject constructor(
    private val accountsAct: AccountsAct,
    private val accBalanceAct: AccBalanceAct,
    private val accountsFlow: AccountsFlow,
    private val accBalanceFlow: AccBalanceFlow,
) : FlowAction<TotalBalanceFlow.Input, Double>() {
    data class Input(
        val withExcluded: Boolean
    )

    override suspend fun Input.createFlow(): Flow<Double> = accountsFlow().map { accs ->
        if (!withExcluded) accs.filter { !it.excluded } else accs
    }.map { includedAccs ->
        includedAccs.map { accBalanceFlow(it) }
    }.map {
        TODO()
    }

    private suspend fun Input.recipe() = accountsAct then { accs ->
        if (!withExcluded) accs.filter { !it.excluded } else accs
    } thenMap accBalanceAct.lambda() then {
        it.sum()
    }
}