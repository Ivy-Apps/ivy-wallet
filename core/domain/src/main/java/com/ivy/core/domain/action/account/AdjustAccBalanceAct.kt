package com.ivy.core.domain.action.account

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.calculate.account.AccBalanceFlow
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.pure.account.adjustBalanceTrn
import com.ivy.data.account.Account
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Adjusts [Account] balance by adding a new "adjust" transaction. The user of the API
 * can choose whether this "adjust" transaction to be hidden or not.
 */
class AdjustAccBalanceAct @Inject constructor(
    private val writeTrnsAct: WriteTrnsAct,
    private val accBalanceFlow: AccBalanceFlow,
    private val timeProvider: TimeProvider,
) : Action<AdjustAccBalanceAct.Input, Unit>() {
    /**
     * @param hideTransaction whether to hide the adjust transactions
     */
    data class Input(
        val account: Account,
        val desiredBalance: Double,
        val hideTransaction: Boolean,
    )

    override suspend fun Input.willDo() {
        val accBalance = accBalanceFlow(AccBalanceFlow.Input(account = account)).first()

        val adjustTrn = adjustBalanceTrn(
            timeProvider = timeProvider,
            account = account,
            currentBalance = accBalance.amount,
            desiredBalance = desiredBalance,
            hiddenTrn = hideTransaction
        )

        if (adjustTrn != null) {
            writeTrnsAct(Modify.save(adjustTrn))
        }
    }
}