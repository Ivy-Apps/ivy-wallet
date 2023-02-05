package com.ivy.core.data

import com.ivy.core.data.common.*
import java.time.LocalDateTime
import java.util.*

data class Budget(
    val id: UUID,
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor,
    val amount: Value,
    val carryOver: Boolean,
    val period: TimePeriod,
    val categories: List<CategoryId>,
    override val orderNum: Double,
    override val lastUpdated: LocalDateTime,
) : Reorderable, Syncable

@JvmInline
value class BudgetId(val id: UUID)
