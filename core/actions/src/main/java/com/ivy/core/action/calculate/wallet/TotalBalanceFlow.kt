package com.ivy.core.action.calculate.wallet

import com.ivy.core.action.FlowAction
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.calculate.account.AccBalanceFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class TotalBalanceFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accBalanceFlow: AccBalanceFlow,
) : FlowAction<TotalBalanceFlow.Input, Double>() {
    data class Input(
        val withExcluded: Boolean
    )

    override suspend fun Input.createFlow(): Flow<Double> = accountsFlow().map { accs ->
        if (!withExcluded) accs.filter { !it.excluded } else accs
    }.map { includedAccs ->
        combine(includedAccs.map { accBalanceFlow(it) }) {
            it.sum()
        }
    }.flattenMerge()
        .flowOn(Dispatchers.Default)
}