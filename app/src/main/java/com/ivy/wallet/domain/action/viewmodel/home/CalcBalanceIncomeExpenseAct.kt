package com.ivy.wallet.domain.action.viewmodel.home

import arrow.core.nonEmptyListOf
import arrow.core.toOption
import com.ivy.fp.action.FPAction
import com.ivy.fp.action.then
import com.ivy.fp.action.thenMap
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
import java.math.BigDecimal
import javax.inject.Inject

class CalcBalanceIncomeExpenseAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct,
    private val exchangeAct: ExchangeAct
) : FPAction<CalcBalanceIncomeExpenseAct.Input, CalcBalanceIncomeExpenseAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
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
        Pair(
            acc,
            foldTransactions(
                transactions = trns,
                valueFunctions = nonEmptyListOf(
                    AccountValueFunctions::balance,
                    AccountValueFunctions::income,
                    AccountValueFunctions::expense
                ),
                arg = acc.id
            )
        )
    } thenMap { (acc, stats) ->
        stats.map {
            exchangeAct(
                ExchangeAct.Input(
                    data = ExchangeData(
                        baseCurrency = baseCurrency,
                        fromCurrency = acc.currency.toOption()
                    ),
                    amount = it
                ),
            ).orZero()
        }
    } then { statsList ->
        Output(
            balance = statsList[0].sumOf { it },
            incomeExpense = IncomeExpensePair(
                income = statsList[1].sumOf { it },
                expense = statsList[2].sumOf { it }
            )
        )
    }

    data class Input(
        val baseCurrency: String,
        val accounts: List<Account>,
        val range: ClosedTimeRange,
    )

    data class Output(
        val balance: BigDecimal,
        val incomeExpense: IncomeExpensePair,
    )
}