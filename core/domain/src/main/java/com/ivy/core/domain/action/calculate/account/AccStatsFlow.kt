package com.ivy.core.domain.action.calculate.account

import arrow.core.getOrElse
import arrow.core.nonEmptyListOf
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.Stats
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.action.transaction.TrnQuery.ActualBetween
import com.ivy.core.domain.action.transaction.TrnQuery.ByAccountId
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.domain.action.transaction.and
import com.ivy.core.domain.functions.exchange.exchange
import com.ivy.core.domain.functions.transaction.foldTransactions
import com.ivy.data.account.Account
import com.ivy.data.time.Period
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccStatsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val exchangeRatesFlow: ExchangeRatesFlow,
) : FlowAction<AccStatsFlow.Input, Stats>() {
    data class Input(
        val account: Account,
        val period: Period,
    )

    override fun Input.createFlow(): Flow<Stats> = combine(
        trnsFlow(ByAccountId(account.id) and ActualBetween(period)),
        exchangeRatesFlow()
    ) { trns, rates ->
        @Suppress("UNUSED_PARAMETER")
        suspend fun income(trn: Transaction, ignored: Unit) =
            if (trn.type == TrnType.Income) {
                exchange(
                    rates = rates,
                    from = trn.value.currency,
                    to = account.currency,
                    amount = trn.value.amount,
                ).getOrElse { 0.0 }
            } else 0.0

        @Suppress("UNUSED_PARAMETER")
        suspend fun expense(trn: Transaction, ignored: Unit) =
            if (trn.type == TrnType.Expense) {
                exchange(
                    rates = rates,
                    from = trn.value.currency,
                    to = account.currency,
                    amount = trn.value.amount,
                ).getOrElse { 0.0 }
            } else 0.0


        @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
        suspend fun countIncome(trn: Transaction, ignored: Unit) =
            if (trn.type == TrnType.Income) 1.0 else 0.0

        @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
        suspend fun countExpense(trn: Transaction, ignored: Unit) =
            if (trn.type == TrnType.Expense) 1.0 else 0.0


        val res = foldTransactions(
            transactions = trns,
            valueFunctions = nonEmptyListOf(
                ::income,
                ::expense,
                ::countIncome,
                ::countExpense
            ),
            arg = Unit
        )
        val income = res[0]
        val expense = res[1]

        Stats(
            balance = income - expense,
            income = income,
            expense = expense,
            incomesCount = res[2].toInt(),
            expensesCount = res[3].toInt(),
        )
    }.flowOn(Dispatchers.Default)
}