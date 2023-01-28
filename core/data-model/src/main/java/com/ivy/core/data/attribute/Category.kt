package com.ivy.core.data.attribute

import com.ivy.core.data.Archiveable
import com.ivy.core.data.ItemVisuals
import com.ivy.core.data.Reorderable
import java.util.*

data class Category(
    val id: UUID,
    val visuals: ItemVisuals,
    val type: CategoryType,
    val parentCategory: CategoryId?,
    override val orderNum: Double,
    override val archived: Boolean,
) : Reorderable, Archiveable

@JvmInline
value class CategoryId(val id: UUID)

enum class CategoryType {
    Income, Expense, Both
}