package com.ivy.core.domain.api.data

import com.ivy.core.data.common.NonNegativeInt
import com.ivy.core.data.common.Value

data class Stats(
    val income: Value,
    val expense: Value,
    val incomesCount: NonNegativeInt,
    val expensesCount: NonNegativeInt,
)