package com.ivy.core.data

import java.time.LocalDateTime
import java.util.*

data class Budget(
    val id: UUID,
    val visuals: ItemVisuals,
    val amount: Value,
    val carryOver: Boolean,
    val startDate: LocalDateTime,
    val interval: BudgetInterval,
    val categories: List<CategoryId>,
    override val orderNum: Double
) : Reorderable

enum class BudgetInterval {
    Weekly, Monthly, Yearly
}