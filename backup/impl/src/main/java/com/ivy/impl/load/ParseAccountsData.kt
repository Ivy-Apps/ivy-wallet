package com.ivy.impl.load

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.maybe
import com.ivy.backup.base.parseItems
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.data.account.Account
import com.ivy.data.account.AccountFolder
import com.ivy.data.account.AccountState
import org.json.JSONObject

// region Accounts
internal fun parseAccounts(
    json: JSONObject,
    timeProvider: TimeProvider,
): Either<ImportBackupError.Parse, List<Account>> =
    parseItems(
        json = json,
        key = "accounts",
        error = ImportBackupError.Parse::Accounts,
        parse = {
            Account(
                id = getString("id").toUUID(),
                name = getString("name"),
                currency = getString("currency"),
                color = getInt("color"),
                icon = maybe { getString("icon") },
                excluded = getBoolean("excluded"),
                folderId = maybe { getString("folderId").toUUID() },
                orderNum = getDouble("orderNum"),
                state = getInt("state").let(AccountState::fromCode)
                    ?: AccountState.Default,
                sync = parseSync(timeProvider)
            )
        }
    )

// endregion

// region Account Folders
internal fun parseAccountFolders(
    json: JSONObject,
    timeProvider: TimeProvider,
): Either<ImportBackupError.Parse, List<AccountFolder>> =
    parseItems(
        json = json,
        key = "accountFolders",
        error = ImportBackupError.Parse::AccountFolders,
        parse = {
            AccountFolder(
                id = getString("id"),
                name = getString("name"),
                color = getInt("color"),
                icon = getString("icon"),
                orderNum = getDouble("orderNum"),
                sync = parseSync(timeProvider)
            )
        }
    )
// endregion