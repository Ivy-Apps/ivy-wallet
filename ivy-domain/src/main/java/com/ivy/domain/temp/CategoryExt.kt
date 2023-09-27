package com.ivy.domain.temp

import com.ivy.domain.datamodel.Category
import com.ivy.persistence.db.entity.CategoryEntity

fun CategoryEntity.toDomain(): Category = Category(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)