package com.ivy.old.parse

import arrow.core.Either
import com.ivy.backup.base.optional
import com.ivy.common.toUUID
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.old.ImportOldDataError
import org.json.JSONObject
import java.time.LocalDateTime

internal fun parseCategories(
    json: JSONObject,
    now: LocalDateTime
): Either<ImportOldDataError, List<Category>> =
    Either.catch(ImportOldDataError.Parse::Categories) {
        val categoriesJson = json.getJSONArray("categories")
        val categories = mutableListOf<Category>()
        for (i in 0 until categoriesJson.length()) {
            val catJson = categoriesJson.getJSONObject(i)
            categories.add(catJson.parseCategory(now))
        }
        categories
    }

private fun JSONObject.parseCategory(
    now: LocalDateTime
): Category = Category(
    id = getString("id").toUUID(),
    name = getString("name"),
    type = CategoryType.Both,
    parentCategoryId = null,
    orderNum = getDouble("orderNum"),
    color = getInt("color"),
    icon = optional { getString("icon") },
    state = CategoryState.Default,
    sync = Sync(
        state = SyncState.Syncing,
        lastUpdated = now
    )
)