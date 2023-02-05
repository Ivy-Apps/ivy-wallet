package com.ivy.core.data.calculation

import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.NonNegativeInt
import com.ivy.core.data.common.PositiveDouble
import java.time.LocalDateTime

data class RawStats(
    val incomes: Map<AssetCode, PositiveDouble>,
    val expenses: Map<AssetCode, PositiveDouble>,
    val incomesCount: NonNegativeInt,
    val expensesCount: NonNegativeInt,
    val newestTransaction: LocalDateTime,
)