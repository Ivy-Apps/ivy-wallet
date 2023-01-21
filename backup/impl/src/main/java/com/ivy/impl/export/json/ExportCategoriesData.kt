package com.ivy.impl.export.json

import com.ivy.core.persistence.dao.category.CategoryDao
import org.json.JSONArray

internal suspend fun exportCategoriesJson(
    categoryDao: CategoryDao
): JSONArray = exportJson(
    findAll = categoryDao::findAllBlocking,
    json = {
        put("id", it.id)
        put("name", it.name)
        put("color", it.color)
        put("icon", it.color)
        put("orderNum", it.orderNum)
        put("parentCategoryId", it.parentCategoryId)
        put("type", it.type.code)
        put("state", it.state.code)
        putSync(it.sync, it.lastUpdated)
    }
)