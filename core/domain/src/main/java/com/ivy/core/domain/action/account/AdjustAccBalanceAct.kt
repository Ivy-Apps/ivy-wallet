package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.Action
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AdjustAccBalanceAct @Inject constructor(
    private val writeTrnsAct: WriteTrnsAct,
    private val accBalanceFlow: AccBalanceFlow,
) : Action<AdjustAccBalanceAct.Input, Unit>() {
    data class Input(
        val account: Account,
        val desiredBalance: Double,
        val hideTransaction: Transaction,
    )

    override suspend fun Input.willDo() {
        val accBalance = accBalanceFlow(AccBalanceFlow.Input(account = account)).first()

        // TODO:
    }
}