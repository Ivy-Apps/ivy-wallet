package com.ivy.temp.persistence

import com.ivy.core.functions.icon.iconId
import com.ivy.data.category.Category
import com.ivy.wallet.io.persistence.data.CategoryEntity

fun mapToEntity(cat: Category): CategoryEntity = CategoryEntity(
    id = cat.id,
    name = cat.name,
    parentCategoryId = cat.parentCategoryId,
    color = cat.color,
    icon = cat.icon.iconId(),
    orderNum = cat.metadata.orderNum,
    isSynced = cat.metadata.sync.isSynced,
    isDeleted = cat.metadata.sync.isDeleted
)