package com.ivy.core.domain.pure.mapping.entity

import com.ivy.core.domain.pure.util.iconId
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
        type = type,
        sync = sync,
    )
}