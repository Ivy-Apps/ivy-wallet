package com.ivy.formula.domain.action

import arrow.core.NonEmptyList
import com.ivy.common.toNonEmptyList
import com.ivy.core.domain.action.FlowAction
import com.ivy.formula.domain.data.formula.Formula
import com.ivy.formula.domain.data.formula.FormulaInput
import com.ivy.formula.domain.pure.parse.compileFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FormulaFlow @Inject constructor(
    private val dataSourceFlow: DataSourceFlow
) : FlowAction<Formula, Double>() {
    override fun createFlow(input: Formula): Flow<Double> = executeFormula(input)

    private fun executeFormula(formula: Formula): Flow<Double> =
        executeFunction(
            input = provideInput(formula.input),
            function = formula.function
        )

    private fun provideInput(
        input: NonEmptyList<FormulaInput>
    ): Flow<NonEmptyList<Double>> {
        val inputFlows = input.map {
            when (it) {
                is FormulaInput.OtherFormula -> executeFormula(it.formula)
                is FormulaInput.Source -> dataSourceFlow(it.source)
                is FormulaInput.Value -> flowOf(it.value)
            }
        }

        return combine(inputFlows) {
            it.toList().toNonEmptyList()
        }
    }

    private fun executeFunction(
        input: Flow<NonEmptyList<Double>>,
        function: String,
    ): Flow<Double> = compileFunction(function).invoke(input)
}