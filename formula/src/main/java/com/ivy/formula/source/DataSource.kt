package com.ivy.formula.source

import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

enum class Stat {
    Balance, Income, Expense, IncomeCount, ExpenseCount, BalanceCount
}

lateinit var trnsFlow: TrnsFlow
lateinit var calculateFlow: CalculateFlow


/*
    1. Filter (Accounts, Categories, ...)
    2. Output currency (default to base)
    3. Stats flow
    4. Select the thing from stats flow
    Filter + Output currency => Flow<Stats>
 */
data class DataSource(
    val filter: TrnQuery,
    val focused: Stat,
)

@OptIn(FlowPreview::class)
fun create(dataSource: DataSource): Flow<Double> =
    trnsFlow(dataSource.filter).flatMapMerge { trns ->
        calculateFlow(
            CalculateFlow.Input(
                trns = trns,
                includeTransfers = false,
                includeHidden = false
            )
        )
    }.map { stats ->
        when (dataSource.focused) {
            Stat.Balance -> stats.balance.amount
            Stat.Income -> stats.income.amount
            Stat.Expense -> stats.expense.amount
            Stat.IncomeCount -> stats.incomesCount.toDouble()
            Stat.ExpenseCount -> stats.expensesCount.toDouble()
            Stat.BalanceCount -> (stats.incomesCount - stats.expensesCount).toDouble()
        }
    }