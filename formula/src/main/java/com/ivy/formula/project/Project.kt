package com.ivy.formula.project

import arrow.core.NonEmptyList
import com.ivy.formula.Formula

data class Project(
    val info: ProjectInfo,
    val formulas: NonEmptyList<Formula>,
    val visualize: NonEmptyList<Formula>
)