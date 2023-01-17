package com.ivy.formula.domain.action

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.formula.domain.data.source.CalculationThing
import com.ivy.formula.domain.data.source.CalculationType
import com.ivy.formula.domain.data.source.DataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataSourceFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val calculateFlow: CalculateFlow
) : FlowAction<DataSource, Double>() {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun createFlow(input: DataSource): Flow<Double> =
        trnsFlow(input.filter).flatMapLatest { trns ->
            calculateFlow(
                CalculateFlow.Input(
                    trns = trns,
                    includeTransfers = false,
                    includeHidden = false,
                    outputCurrency = input.calculation.outputCurrency
                )
            )
        }.map { stats ->
            val byValue = input.calculation.type == CalculationType.ByValue
            when (input.calculation.thing) {
                CalculationThing.Income ->
                    if (byValue) stats.income.amount else stats.incomesCount
                CalculationThing.Expense ->
                    if (byValue) stats.expense.amount else stats.expensesCount
                CalculationThing.Balance -> if (byValue)
                    stats.balance.amount else (stats.incomesCount - stats.expensesCount)
            }.toDouble()
        }
}