package com.ivy.old.parse

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.maybe
import com.ivy.backup.base.parseItems
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
): Either<ImportBackupError.Parse, List<Account>> = parseItems(
    json = json,
    key = "accounts",
    error = ImportBackupError.Parse::Accounts,
    parse = {
        parseAccount(now)
    }
)

private fun JSONObject.parseAccount(
    now: LocalDateTime
): Account = Account(
    id = getString("id").toUUID(),
    name = getString("name"),
    currency = getString("currency"),
    color = getInt("color"),
    icon = maybe { getString("icon") },
    excluded = getBoolean("includeInBalance").not(),
    folderId = null,
    orderNum = getDouble("orderNum"),
    state = AccountState.Default,
    sync = Sync(
        state = SyncState.Syncing,
        lastUpdated = now
    )
)