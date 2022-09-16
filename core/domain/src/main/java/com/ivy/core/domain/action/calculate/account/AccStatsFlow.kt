package com.ivy.core.domain.action.calculate.account

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.calculate.Stats
import com.ivy.core.domain.action.transaction.TrnQuery.ActualBetween
import com.ivy.core.domain.action.transaction.TrnQuery.ByAccountId
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.domain.action.transaction.and
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import com.ivy.data.time.Period
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import javax.inject.Inject

class AccStatsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val calculateFlow: CalculateFlow,
) : FlowAction<AccStatsFlow.Input, Stats>() {
    data class Input(
        val account: Account,
        val period: Period,
        val outputCurrency: CurrencyCode = account.currency,
    )

    @OptIn(FlowPreview::class)
    override fun Input.createFlow(): Flow<Stats> =
        trnsFlow(ByAccountId(account.id) and ActualBetween(period))
            .flatMapMerge { trns ->
                calculateFlow(
                    CalculateFlow.Input(
                        trns = trns,
                        outputCurrency = outputCurrency,
                        includeTransfers = true
                    )
                )
            }
}