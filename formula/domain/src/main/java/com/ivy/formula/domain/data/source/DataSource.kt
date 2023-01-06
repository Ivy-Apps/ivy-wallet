package com.ivy.formula.domain.data.source

import com.ivy.core.domain.action.transaction.TrnQuery

data class DataSource(
    val filter: TrnQuery,
    val calculation: Calculation,
)