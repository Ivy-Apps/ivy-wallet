package com.ivy.core.domain.action.calculate.wallet

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.calculate.account.AccStatsFlow
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.time.allTime
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class TotalBalanceFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accStatsFlow: AccStatsFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowAction<TotalBalanceFlow.Input, Value>() {
    data class Input(
        val withExcludedAccs: Boolean,
        val outputCurrency: CurrencyCode,
    )

    override fun Input.createFlow(): Flow<Value> = accountsFlow().map { accs ->
        if (!withExcludedAccs) accs.filter { !it.excluded } else accs
    }.map { accs ->
        baseCurrencyFlow().flatMapMerge { baseCurrency ->
            combine(accs.map {
                accStatsFlow(
                    AccStatsFlow.Input(
                        account = it,
                        period = allTime(),
                        outputCurrency = baseCurrency
                    )
                )
            }) { stats ->
                val totalBalance = stats.fold(initial = 0.0) { totalBalance, accStats ->
                    totalBalance + accStats.balance.amount
                }
                Value(
                    amount = totalBalance,
                    currency = baseCurrency
                )
            }
        }
    }.flattenMerge()
        .flowOn(Dispatchers.Default)

}