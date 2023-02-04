package com.ivy.core.data

import com.ivy.core.data.common.Archiveable
import com.ivy.core.data.common.ItemIconId
import com.ivy.core.data.common.IvyColor
import com.ivy.core.data.common.Reorderable
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
) : Reorderable, Archiveable

@JvmInline
value class CategoryId(val id: UUID)

enum class CategoryType {
    Income, Expense, Both
}