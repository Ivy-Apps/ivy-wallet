package com.ivy.core.domain.action.calculate.account

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.pure.time.allTime
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import com.ivy.data.account.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Calculates account's balance. Including hidden and transfer transactions.
 */
@Deprecated("inefficient - will be replaced with `account-cache` algo")
class AccBalanceFlow @Inject constructor(
    private val accStatsFlow: AccStatsFlow,
) : FlowAction<AccBalanceFlow.Input, Value>() {

    @Deprecated("inefficient - will be replaced with `account-cache` algo")
    data class Input(
        val account: Account,
        val outputCurrency: CurrencyCode = account.currency,
    )

    override fun createFlow(input: Input): Flow<Value> = accStatsFlow(
        AccStatsFlow.Input(
            account = input.account,
            range = allTime(),
            includeHidden = true,
            outputCurrency = input.outputCurrency
        )
    ).map { stats ->
        stats.balance
    }
}