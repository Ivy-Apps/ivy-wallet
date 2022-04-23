package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.fp.action.FPAction
import com.ivy.fp.action.lambda
import com.ivy.fp.action.then
import com.ivy.fp.then
import com.ivy.wallet.domain.action.account.AccountByIdAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.exchange.actInput
import com.ivy.wallet.domain.action.transaction.DueTrnsAct
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.domain.pure.exchange.ExchangeTrnArgument
import com.ivy.wallet.domain.pure.exchange.exchangeInCurrency
import com.ivy.wallet.domain.pure.transaction.expenses
import com.ivy.wallet.domain.pure.transaction.incomes
import com.ivy.wallet.domain.pure.transaction.sumTrns
import com.ivy.wallet.utils.timeNowUTC
import java.time.LocalDateTime
import javax.inject.Inject

class DueTrnsInfoAct @Inject constructor(
    private val dueTrnsAct: DueTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val exchangeAct: ExchangeAct
) : FPAction<DueTrnsInfoAct.Input, DueTrnsInfoAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output = suspend {
        range
    } then dueTrnsAct then { trns ->
        val timeNow = timeNowUTC()
        trns.filter {
            this.dueFilter(it, timeNow)
        }
    } then { upcomingTrns ->
        //We have due transactions in different currencies
        val exchangeArg = ExchangeTrnArgument(
            baseCurrency = baseCurrency,
            exchange = ::actInput then exchangeAct,
            getAccount = accountByIdAct.lambda()
        )

        io {
            Output(
                dueIncomeExpense = IncomeExpensePair(
                    income = sumTrns(
                        incomes(upcomingTrns),
                        ::exchangeInCurrency,
                        exchangeArg
                    ),
                    expense = sumTrns(
                        expenses(upcomingTrns),
                        ::exchangeInCurrency,
                        exchangeArg
                    )
                ),
                dueTrns = upcomingTrns
            )
        }
    }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String,
        val dueFilter: (Transaction, LocalDateTime) -> Boolean
    )

    data class Output(
        val dueIncomeExpense: IncomeExpensePair,
        val dueTrns: List<Transaction>
    )
}