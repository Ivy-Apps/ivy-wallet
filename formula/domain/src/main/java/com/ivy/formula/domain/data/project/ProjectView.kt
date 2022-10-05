package com.ivy.formula.domain.data.project

import arrow.core.NonEmptyList
import com.ivy.formula.domain.data.formula.Formula

data class ProjectView(
    val formulas: NonEmptyList<Formula>
)