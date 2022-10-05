package com.ivy.formula.domain.project

import arrow.core.NonEmptyList
import com.ivy.formula.domain.formula.Formula

data class ProjectView(
    val formulas: NonEmptyList<Formula>
)