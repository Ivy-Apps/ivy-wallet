package com.ivy.old.parse

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.maybe
import com.ivy.backup.base.parseItems
import com.ivy.common.toUUID
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import org.json.JSONObject
import java.time.LocalDateTime

internal fun parseCategories(
    json: JSONObject,
    now: LocalDateTime
): Either<ImportBackupError.Parse, List<Category>> = parseItems(
    json = json,
    key = "categories",
    error = ImportBackupError.Parse::Categories,
    parse = {
        parseCategory(now)
    }
)

private fun JSONObject.parseCategory(
    now: LocalDateTime
): Category = Category(
    id = getString("id").toUUID(),
    name = getString("name"),
    type = CategoryType.Both,
    parentCategoryId = null,
    orderNum = getDouble("orderNum"),
    color = getInt("color"),
    icon = maybe { getString("icon") },
    state = CategoryState.Default,
    sync = Sync(
        state = SyncState.Syncing,
        lastUpdated = now
    )
)