package com.ivy.core.persistence.dummy.category

import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.data.SyncState
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import java.time.Instant
import java.util.*

fun dummyCategoryEntity(
    id: String = UUID.randomUUID().toString(),
    name: String = "Category",
    color: Int = 123123,
    icon: String? = "icon",
    orderNum: Double = 0.0,
    parentCategoryId: String? = null,
    state: CategoryState = CategoryState.Default,
    type: CategoryType = CategoryType.Both,
    sync: SyncState = SyncState.Synced,
    lastUpdated: Instant = Instant.now(),
) = CategoryEntity(
    id = id,
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    parentCategoryId = parentCategoryId,
    state = state,
    type = type,
    sync = sync,
    lastUpdated = lastUpdated,
)