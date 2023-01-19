package com.ivy.core.domain.algorithm.balance

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.algorithm.accountcache.RawAccStatsFlow
import com.ivy.core.domain.algorithm.calc.RatesFlow
import com.ivy.core.domain.algorithm.calc.exchangeRawStats
import com.ivy.data.Value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TotalBalanceFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val ratesFlow: RatesFlow,
    private val rawAccStatsFlow: RawAccStatsFlow,
) : FlowAction<TotalBalanceFlow.Input, Value>() {
    @JvmInline
    value class Input(
        val withExcluded: Boolean,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(input: Input): Flow<Value> = accountsFlow().flatMapLatest { accounts ->
        val included = if (input.withExcluded) accounts else accounts.filter { !it.excluded }
        if (included.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(
                included.map { rawAccStatsFlow(it.id.toString()) }
            ) { includedRawStats ->
                includedRawStats.toList()
            }
        }
    }.flatMapLatest { includedRawStats ->
        ratesFlow().map { rates ->
            val balanceAmount = includedRawStats.sumOf { accRawStats ->
                val stats = exchangeRawStats(
                    rawStats = accRawStats,
                    rates = rates,
                    outputCurrency = rates.baseCurrency,
                )
                stats.income.amount - stats.expense.amount
            }
            Value(balanceAmount, rates.baseCurrency)
        }
    }
}