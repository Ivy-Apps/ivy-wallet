package com.ivy.core.data

import com.ivy.core.data.common.*
import java.time.LocalDateTime
import java.util.*

data class Category(
    val id: UUID,
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor,
    val type: CategoryType,
    val parentCategory: CategoryId?,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class CategoryId(val id: UUID)

enum class CategoryType {
    Income, Expense, Both
}