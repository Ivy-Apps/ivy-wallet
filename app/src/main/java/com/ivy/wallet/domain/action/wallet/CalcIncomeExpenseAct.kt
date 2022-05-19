package com.ivy.wallet.domain.action.wallet

import arrow.core.nonEmptyListOf
import arrow.core.toOption
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.wallet.domain.action.account.AccTrnsAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.pure.account.filterExcluded
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.transaction.AccountValueFunctions
import com.ivy.wallet.domain.pure.transaction.foldTransactions
import com.ivy.wallet.domain.pure.util.orZero
import timber.log.Timber
import javax.inject.Inject

class CalcIncomeExpenseAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct,
    private val exchangeAct: ExchangeAct
) : FPAction<CalcIncomeExpenseAct.Input, IncomeExpensePair>() {

    override suspend fun Input.compose(): suspend () -> IncomeExpensePair = suspend {
        filterExcluded(accounts)
    } thenMap { acc ->
        Pair(
            acc,
            accTrnsAct(
                AccTrnsAct.Input(
                    accountId = acc.id,
                    range = range
                )
            )
        )
    } thenMap { (acc, trns) ->
        Timber.i("acc: $acc, trns = ${trns.size}")
        Pair(
            acc,
            foldTransactions(
                transactions = trns,
                valueFunctions = nonEmptyListOf(
                    AccountValueFunctions::income,
                    AccountValueFunctions::expense
                ),
                arg = acc.id
            )
        )
    } thenMap { (acc, stats) ->
        Timber.i("acc_stats: $acc - $stats")
        stats.map {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = (acc.currency ?: baseCurrency).toOption()
                    ),
                    amount = it
                ),
            ).orZero()
        }
    } then { statsList ->
        IncomeExpensePair(
            income = statsList.sumOf { it[0] },
            expense = statsList.sumOf { it[1] }
        )
    }

    data class Input(
        val baseCurrency: String,
        val accounts: List<Account>,
        val range: ClosedTimeRange,
    )
}