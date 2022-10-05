package com.ivy.formula.domain.data.formula

import com.ivy.formula.domain.data.source.DataSource

sealed interface FormulaInput {
    data class Value(val value: Double) : FormulaInput
    data class OtherFormula(val formula: Formula) : FormulaInput
    data class Source(val source: DataSource) : FormulaInput
}