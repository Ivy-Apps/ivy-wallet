package com.ivy.wallet.domain.action.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.action.account.AccTrnsAct
import com.ivy.wallet.domain.action.then
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.fp.account.AccountValueFunctions
import com.ivy.wallet.domain.fp.account.calcAccValues
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccBalanceAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct
) : Action<CalcAccBalanceAct.Input, CalcAccBalanceAct.Output>() {

    override suspend fun Input.willDo(): Output = io {
        val composition = accTrnsAct then { accTrns ->
            Output(
                account = account,
                balance = calcAccValues(
                    accountId = account.id,
                    accountsTrns = accTrns,
                    valueFunctions = nonEmptyListOf(AccountValueFunctions::balance)
                ).head
            )
        }

        composition(
            AccTrnsAct.Input(
                accountId = account.id,
                range = ClosedTimeRange.allTimeIvy()
            )
        )

    }

    data class Input(
        val account: Account
    )

    data class Output(
        val account: Account,
        val balance: BigDecimal
    )
}