package com.ivy.core.domain.pure.dummy

import androidx.annotation.ColorInt
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import java.time.LocalDateTime
import java.util.*

fun dummyCategory(
    id: UUID = UUID.randomUUID(),
    name: String = "Dummy Category",
    parentCategoryId: UUID? = null,
    @ColorInt
    color: Int = 1,
    icon: ItemIconId = "category",
    sync: SyncState = SyncState.Synced,
    orderNum: Double = 0.0,
    type: CategoryType = CategoryType.Both,
    state: CategoryState = CategoryState.Default,
    lastUpdated: LocalDateTime = LocalDateTime.now(),
): Category = Category(
    id = id,
    name = name,
    parentCategoryId = parentCategoryId,
    color = color,
    icon = icon,
    sync = Sync(sync, lastUpdated),
    orderNum = orderNum,
    type = type,
    state = state,
)
