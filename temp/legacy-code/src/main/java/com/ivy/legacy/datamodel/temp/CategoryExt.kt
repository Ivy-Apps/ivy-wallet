package com.ivy.legacy.datamodel.temp

import com.ivy.data.db.entity.CategoryEntity
import com.ivy.legacy.datamodel.Category

fun CategoryEntity.toLegacyDomain(): Category = Category(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)
