package com.ivy.impl.export.json

import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import org.json.JSONArray

internal suspend fun exportAccountsJson(
    accountDao: AccountDao
): JSONArray = exportJson(
    findAll = accountDao::findAllBlocking,
    json = {
        put("id", it.id)
        put("name", it.name)
        put("currency", it.currency)
        put("color", it.color)
        put("icon", it.icon)
        put("folderId", it.folderId)
        put("orderNum", it.orderNum)
        put("excluded", it.excluded)
        put("state", it.state.code)
        putSync(it.sync, it.lastUpdated)
    },
)

internal suspend fun exportAccountFoldersJson(
    accountFolderDao: AccountFolderDao
): JSONArray = exportJson(
    findAll = accountFolderDao::findAllBlocking,
    json = {
        put("id", it.id)
        put("name", it.name)
        put("color", it.color)
        put("icon", it.icon)
        put("orderNum", it.orderNum)
        putSync(it.sync, it.lastUpdated)
    }
)