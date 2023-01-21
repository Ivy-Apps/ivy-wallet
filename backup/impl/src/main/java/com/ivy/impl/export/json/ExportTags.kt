package com.ivy.impl.export.json

import com.ivy.core.persistence.dao.tag.TagDao
import org.json.JSONArray

internal suspend fun exportTagsToJson(
    tagDao: TagDao
): JSONArray = exportJson(
    findAll = tagDao::findAllBlocking,
    json = {
        put("id", it.id)
        put("color", it.color)
        put("name", it.name)
        put("orderNum", it.orderNum)
        put("state", it.state.code)
        putSync(it.sync, it.lastUpdated)
    }
)