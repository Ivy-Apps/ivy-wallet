package com.ivy.core.data

import arrow.core.NonEmptyList
import com.ivy.core.data.common.*
import com.ivy.core.data.sync.Syncable
import java.time.LocalDateTime
import java.util.*

data class Budget(
    override val id: UUID,
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor,
    val amount: Value,
    val carryOver: Boolean,
    val period: TimePeriod,
    val categories: BudgetCategories,
    val accounts: BudgetAccounts,
    override val orderNum: Double,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Reorderable, Syncable

sealed interface BudgetCategories {
    object All : BudgetCategories
    data class Specific(val ids: NonEmptyList<CategoryId>) : BudgetCategories
}

sealed interface BudgetAccounts {
    object All : BudgetAccounts
    data class Specific(val ids: NonEmptyList<AccountId>) : BudgetAccounts
}

@JvmInline
value class BudgetId(val id: UUID)
