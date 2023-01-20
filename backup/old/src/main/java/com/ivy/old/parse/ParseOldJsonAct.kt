package com.ivy.old.parse

import arrow.core.Either
import arrow.core.computations.either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.data.BackupData
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class ParseOldJsonAct @Inject constructor(
    private val timeProvider: TimeProvider,
) : Action<JSONObject, Either<ImportBackupError, BackupData>>() {
    override fun dispatcher() = Dispatchers.Default

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(json: JSONObject): Either<ImportBackupError, BackupData> = either {
        val now = timeProvider.timeNow()
        val accounts = parseAccounts(json, now).bind()
        val categories = parseCategories(json, now).bind()

        val accountsMap = accounts.associateBy { it.id.toString() }
        val categoriesMap = categories.associateBy { it.id.toString() }
        val transactions = parseTransactions(
            json = json,
            now = now,
            accountsMap = accountsMap,
            categoriesMap = categoriesMap,
            timeProvider = timeProvider,
        ).bind()
        val transfersData = parseTransfers(
            json = json,
            now = now,
            accountsMap = accountsMap,
            categoriesMap = categoriesMap,
            timeProvider = timeProvider,
        ).bind()
        val settings = parseSettings(json).bind()

        BackupData(
            accounts = accounts,
            categories = categories,
            transactions = transactions + transfersData.partlyCorrupted,
            transfers = transfersData.transfers,

            accountFolders = null,
            tags = null,
            attachments = null,

            settings = settings,
        )
    }
}