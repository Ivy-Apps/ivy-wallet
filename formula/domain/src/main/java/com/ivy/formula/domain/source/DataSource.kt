package com.ivy.formula.domain.source

import com.ivy.core.domain.action.transaction.TrnQuery

data class DataSource(
    val filter: TrnQuery,
    val calculation: Calculation,
)