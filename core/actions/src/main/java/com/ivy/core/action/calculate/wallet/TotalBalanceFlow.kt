package com.ivy.core.action.calculate.wallet

import arrow.core.getOrElse
import com.ivy.core.action.FlowAction
import com.ivy.core.action.account.AccountsFlow
import com.ivy.core.action.calculate.account.AccBalanceFlow
import com.ivy.core.action.currency.exchange.ExchangeFlow
import com.ivy.data.CurrencyCode
import com.ivy.data.account.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class TotalBalanceFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accBalanceFlow: AccBalanceFlow,
    private val exchangeFlow: ExchangeFlow,
) : FlowAction<TotalBalanceFlow.Input, Double>() {
    data class Input(
        val withExcludedAccs: Boolean,
        val outputCurrency: CurrencyCode,
    )

    override suspend fun Input.createFlow(): Flow<Double> = accountsFlow().map { accs ->
        if (!withExcludedAccs) accs.filter { !it.excluded } else accs
    }.map { includedAccs ->
        totalBalanceFlow(accs = includedAccs, outputCurrency = outputCurrency)
    }.flattenMerge()
        .flowOn(Dispatchers.Default)

    private suspend fun totalBalanceFlow(
        accs: List<Account>,
        outputCurrency: CurrencyCode,
    ): Flow<Double> = combine(
        // produces flows of not exchanged account balances
        accs.map { acc ->
            accBalanceFlow(acc).map { accBalance -> acc to accBalance }
        }
    ) { accBalanceArr ->
        combine(accBalanceArr.map { (acc, balance) ->
            // produces a flow that exchanges account balance to output currency for each account
            exchangeFlow(
                ExchangeFlow.Input(
                    from = acc.currency,
                    to = outputCurrency,
                    amount = balance
                )
            )
        }) { balances ->
            // folds the latest emitted account balances
            balances.sumOf { it.getOrElse { 0.0 } }
        }
    }.flattenMerge()
}