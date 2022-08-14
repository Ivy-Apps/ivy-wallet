package com.ivy.core.action.calculate.wallet

import com.ivy.core.action.account.AccountsAct
import com.ivy.core.action.calculate.account.AccBalanceAct
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.fixUnit
import com.ivy.frp.lambda
import com.ivy.frp.then
import javax.inject.Inject

class TotalBalanceAct @Inject constructor(
    private val accountsAct: AccountsAct,
    private val accBalanceAct: AccBalanceAct
) : FPAction<TotalBalanceAct.Input, Double>() {
    data class Input(
        val withExcluded: Boolean
    )

    override suspend fun Input.compose(): suspend () -> Double = recipe().fixUnit()

    private suspend fun Input.recipe() = accountsAct then { accs ->
        if (!withExcluded) accs.filter { !it.excluded } else accs
    } thenMap accBalanceAct.lambda() then {
        it.sum()
    }
}