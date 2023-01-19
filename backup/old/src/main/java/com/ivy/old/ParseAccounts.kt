package com.ivy.old

import arrow.core.Either
import com.ivy.backup.base.optional
import com.ivy.common.toUUID
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import org.json.JSONObject
import java.time.LocalDateTime

internal fun parseAccounts(
    json: JSONObject,
    now: LocalDateTime
): Either<ImportOldDataError, List<Account>> =
    Either.catch(ImportOldDataError.Parse::Accounts) {
        val accountsJson = json.getJSONArray("accounts")
        val accounts = mutableListOf<Account>()
        for (i in 0 until accountsJson.length()) {
            val accJson = accountsJson.getJSONObject(i)
            accounts.add(accJson.parseAccount(now))
        }
        accounts
    }

private fun JSONObject.parseAccount(
    now: LocalDateTime
): Account = Account(
    id = getString("id").toUUID(),
    name = getString("name"),
    currency = getString("currency"),
    color = getInt("color"),
    icon = optional { getString("icon") },
    excluded = getBoolean("includeInBalance").not(),
    folderId = null,
    orderNum = getDouble("orderNum"),
    state = AccountState.Default,
    sync = Sync(
        state = SyncState.Syncing,
        lastUpdated = now
    )
)