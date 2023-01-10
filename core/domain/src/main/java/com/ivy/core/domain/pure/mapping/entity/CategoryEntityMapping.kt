package com.ivy.core.domain.pure.mapping.entity

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.data.category.Category

fun mapToEntity(category: Category, timeProvider: TimeProvider) = with(category) {
    CategoryEntity(
        id = id.toString(),
        name = name,
        color = color,
        icon = icon,
        orderNum = orderNum,
        parentCategoryId = parentCategoryId?.toString(),
        state = state,
        type = type,
        sync = sync.state,
        lastUpdated = sync.lastUpdated.toUtc(timeProvider),
    )
}