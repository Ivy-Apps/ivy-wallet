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
import com.ivy.data.time.TimeRange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Calculates account's incomes and expenses **including transfer transactions**.
 * The inclusion of hidden transactions is chosen by the user of this API.
 */
@Deprecated("inefficient - will be replaced with `account-cache` algo")
class AccStatsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val calculateFlow: CalculateFlow,
) : FlowAction<AccStatsFlow.Input, Stats>() {
    @Deprecated("inefficient - will be replaced with `account-cache` algo")
    /**
     * @param outputCurrency the desired currency of the result, **defaults to account's currency**
     */
    data class Input(
        val account: Account,
        val range: TimeRange,
        val includeHidden: Boolean,
        val outputCurrency: CurrencyCode = account.currency,
    )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun createFlow(input: Input): Flow<Stats> =
        trnsFlow(ByAccountId(input.account.id) and ActualBetween(input.range))
            .flatMapLatest { trns ->
                calculateFlow(
                    CalculateFlow.Input(
                        trns = trns,
                        outputCurrency = input.outputCurrency,
                        includeHidden = input.includeHidden,
                        includeTransfers = true
                    )
                )
            }
}