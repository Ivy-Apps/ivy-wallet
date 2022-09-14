package com.ivy.core.domain.functions.mapping.entity

import com.ivy.core.domain.functions.icon.iconId
import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.data.category.Category

fun mapToEntity(category: Category) = with(category) {
    CategoryEntity(
        id = id.toString(),
        name = name,
        color = color,
        icon = icon.iconId(),
        orderNum = orderNum,
        parentCategoryId = parentCategoryId?.toString(),
        state = state,
        sync = sync,
    )
}