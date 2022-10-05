package com.ivy.formula.domain.data.formula

import arrow.core.NonEmptyList

data class Formula(
    val id: String,
    val displayName: String,
    val input: NonEmptyList<FormulaInput>,
    val function: String,
)