package com.ivy.wallet.domain.data.core

import com.ivy.data.Category
import com.ivy.wallet.io.network.data.CategoryDTO
import com.ivy.wallet.io.persistence.data.CategoryEntity

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)

fun Category.toDTO(): CategoryDTO = CategoryDTO(
    name = name,
    color = color,
    icon = icon,
    orderNum = orderNum,
    id = id
)