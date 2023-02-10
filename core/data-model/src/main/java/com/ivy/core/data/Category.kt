package com.ivy.core.data

import com.ivy.core.data.common.Archiveable
import com.ivy.core.data.common.ItemIconId
import com.ivy.core.data.common.IvyColor
import com.ivy.core.data.common.Reorderable
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId
import java.time.LocalDateTime
import java.util.*

data class Category(
    override val id: CategoryId,
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor,
    val type: CategoryType,
    val parentCategory: CategoryId?,
    override val orderNum: Double,
    override val archived: Boolean,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Reorderable, Archiveable, Syncable

@JvmInline
value class CategoryId(override val uuid: UUID) : UniqueId

enum class CategoryType {
    Income, Expense, Both
}