package com.ivy.formula.domain.formula

import com.ivy.formula.domain.source.DataSource

sealed interface FormulaInput {
    data class Value(val value: Double) : FormulaInput
    data class OtherFormula(val formula: Formula) : FormulaInput
    data class Source(val source: DataSource) : FormulaInput
}