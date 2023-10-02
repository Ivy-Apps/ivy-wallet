package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.Category
import com.ivy.data.db.entity.CategoryEntity

fun CategoryEntity.toDomain(): Category = Category(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)