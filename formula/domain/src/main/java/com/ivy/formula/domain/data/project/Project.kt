package com.ivy.formula.domain.data.project

import arrow.core.NonEmptyList
import com.ivy.data.CurrencyCode
import com.ivy.data.time.TimePeriod
import com.ivy.formula.domain.data.formula.Formula

data class Project(
    val info: ProjectInfo,
    val formulas: NonEmptyList<Formula>,
    val period: TimePeriod,
    val currency: CurrencyCode,
)