package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.common.dateNowUTC
import com.ivy.data.pure.IncomeExpensePair
import com.ivy.data.transaction.TransactionOld
import com.ivy.exchange.deprecated.ExchangeActOld
import com.ivy.exchange.deprecated.actInput
import com.ivy.frp.action.FPAction
import com.ivy.frp.lambda
import com.ivy.frp.then
import com.ivy.wallet.domain.action.account.AccountByIdAct
import com.ivy.wallet.domain.action.transaction.DueTrnsAct
import com.ivy.wallet.domain.pure.exchange.ExchangeTrnArgument
import com.ivy.wallet.domain.pure.exchange.exchangeInBaseCurrency
import com.ivy.wallet.domain.pure.transaction.expenses
import com.ivy.wallet.domain.pure.transaction.incomes
import com.ivy.wallet.domain.pure.transaction.sumTrns
import java.time.LocalDate
import javax.inject.Inject

class DueTrnsInfoAct @Inject constructor(
    private val dueTrnsAct: DueTrnsAct,
    private val accountByIdAct: AccountByIdAct,
    private val exchangeAct: ExchangeActOld
) : FPAction<DueTrnsInfoAct.Input, DueTrnsInfoAct.Output>() {

    override suspend fun Input.compose(): suspend () -> Output =
        suspend {
            range
        } then dueTrnsAct then { trns ->
            val dateNow = dateNowUTC()
            trns.filter {
                this.dueFilter(it, dateNow)
            }
        } then { dueTrns ->
            //We have due transactions in different currencies
            val exchangeArg = ExchangeTrnArgument(
                baseCurrency = baseCurrency,
                exchange = ::actInput then exchangeAct,
                getAccount = accountByIdAct.lambda()
            )

            Output(
                dueIncomeExpense = IncomeExpensePair(
                    income = sumTrns(
                        incomes(dueTrns),
                        ::exchangeInBaseCurrency,
                        exchangeArg
                    ),
                    expense = sumTrns(
                        expenses(dueTrns),
                        ::exchangeInBaseCurrency,
                        exchangeArg
                    )
                ),
                dueTrns = dueTrns
            )
        }

    data class Input(
        val range: com.ivy.base.ClosedTimeRange,
        val baseCurrency: String,
        val dueFilter: (TransactionOld, LocalDate) -> Boolean
    )

    data class Output(
        val dueIncomeExpense: IncomeExpensePair,
        val dueTrns: List<TransactionOld>
    )
}