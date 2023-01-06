package com.ivy.core.domain.action.calculate.wallet

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.calculate.account.AccStatsFlow
import com.ivy.core.domain.action.calculate.wallet.TotalBalanceFlow.Input
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.pure.time.allTime
import com.ivy.core.domain.pure.util.combineSafe
import com.ivy.data.CurrencyCode
import com.ivy.data.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Calculates Ivy Wallet's balance by summing the balances of all accounts.
 * The user of the API can decide whether to include excluded accounts
 * by setting [Input.withExcludedAccs].
 */
@OptIn(FlowPreview::class)
class TotalBalanceFlow @Inject constructor(
    private val accountsFlow: AccountsFlow,
    private val accStatsFlow: AccStatsFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowAction<Input, Value>() {

    /**
     * @param withExcludedAccs whether to include excluded accounts in the balance calculation
     * @param outputCurrency pass **null** for base currency
     */
    data class Input(
        val withExcludedAccs: Boolean,
        val outputCurrency: CurrencyCode? = null,
    )

    override fun Input.createFlow(): Flow<Value> = accountsFlow().map { allAccounts ->
        if (withExcludedAccs) allAccounts else allAccounts.filter { !it.excluded }
    }.flatMapLatest { accs ->
        outputCurrencyFlow().flatMapLatest { outputCurrency ->
            combineSafe(
                flows = accs.map {
                    accStatsFlow(
                        AccStatsFlow.Input(
                            account = it,
                            range = allTime(),
                            includeHidden = true,
                            outputCurrency = outputCurrency,
                        )
                    )
                },
                ifEmpty = Value(amount = 0.0, currency = outputCurrency)
            ) { stats ->
                Value(
                    amount = stats.fold(
                        initial = 0.0
                    ) { totalBalance, accStats ->
                        totalBalance + accStats.balance.amount
                    },
                    currency = outputCurrency
                )
            }
        }
    }.flowOn(Dispatchers.Default)

    private fun Input.outputCurrencyFlow(): Flow<CurrencyCode> =
        outputCurrency?.let(::flowOf) ?: baseCurrencyFlow()
}