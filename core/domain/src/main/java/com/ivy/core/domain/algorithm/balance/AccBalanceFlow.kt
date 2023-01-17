package com.ivy.core.domain.algorithm.balance

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.algorithm.accountcache.RawAccStatsFlow
import com.ivy.core.domain.algorithm.calc.RatesFlow
import com.ivy.core.domain.algorithm.calc.exchangeRawStats
import com.ivy.data.Value
import com.ivy.data.account.Account
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccBalanceFlow @Inject constructor(
    private val rawAccStatsFlow: RawAccStatsFlow,
    private val ratesFlow: RatesFlow,
) : FlowAction<Account, Value>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createFlow(
        account: Account
    ): Flow<Value> = rawAccStatsFlow(account.id.toString()).flatMapLatest { rawStats ->
        ratesFlow().map { rates ->
            val stats = exchangeRawStats(
                rawStats = rawStats,
                rates = rates,
                outputCurrency = account.currency
            )
            Value(
                amount = stats.income.amount - stats.expense.amount,
                currency = account.currency
            )
        }
    }
}