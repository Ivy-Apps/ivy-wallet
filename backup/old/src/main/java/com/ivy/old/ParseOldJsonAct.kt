package com.ivy.old

import arrow.core.Either
import arrow.core.computations.either
import com.ivy.backup.base.BackupData
import com.ivy.backup.base.BatchTransferData
import com.ivy.backup.base.SettingsData
import com.ivy.backup.base.optional
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.toUUID
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.transaction.transfer.TransferData
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Theme
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class ParseOldJsonAct @Inject constructor(
    private val timeProvider: TimeProvider,
) : Action<JSONObject, Either<ImportOldDataError, BackupData>>() {
    override fun dispatcher() = Dispatchers.Default

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(json: JSONObject): Either<ImportOldDataError, BackupData> = either {
        val now = timeProvider.timeNow()
        val accounts = parseAccounts(json, now).bind()
        val categories = parseCategories(json, now).bind()

        val accountsMap = accounts.associateBy { it.id.toString() }
        val categoriesMap = categories.associateBy { it.id.toString() }
        val transactions = parseTransactions(
            json = json,
            now = now,
            accountsMap = accountsMap,
            categoriesMap = categoriesMap
        ).bind()
        val transfers = parseTransfers(
            json = json,
            now = now,
            accountsMap = accountsMap,
            categoriesMap = categoriesMap
        ).bind()
        val settings = parseSettings(json).bind()

        BackupData(
            accounts = accounts,
            categories = categories,
            transactions = transactions,
            transfers = transfers,

            accountFolders = null,
            tags = null,
            attachments = null,

            settings = settings,
        )
    }

    // region Accounts
    private fun parseAccounts(
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
    // endregion

    // region Categories
    private fun parseCategories(
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
    // endregion

    // region Transactions (Incomes & Expenses)
    private fun parseTransactions(
        json: JSONObject,
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): Either<ImportOldDataError, List<Transaction>> =
        Either.catch(ImportOldDataError.Parse::Transactions) {
            val transactionsJson = json.getJSONArray("transactions")
            val transactions = mutableListOf<Transaction>()
            for (i in 0 until transactionsJson.length()) {
                val trnJson = transactionsJson.getJSONObject(i)
                if (trnJson.getString("type") == "TRANSFER")
                    continue // skip transfers
                transactions.add(
                    trnJson.parseTransaction(
                        now = now,
                        accountsMap = accountsMap,
                        categoriesMap = categoriesMap
                    )
                )
            }
            transactions
        }

    private fun JSONObject.parseTransaction(
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): Transaction {
        val account = accountsMap[getString("accountId")]
            ?: error("Account with id ${getString("accountId")} not found")

        return Transaction(
            id = getString("id").toUUID(),
            account = account,
            type = when (val type = getString("type")) {
                "INCOME" -> TransactionType.Income
                "EXPENSE" -> TransactionType.Expense
                else -> error("Unknown transaction type: $type")
            },
            value = Value(
                amount = getDouble("amount"),
                currency = account.currency,
            ),
            category = categoriesMap[getString("categoryId")],
            time = parseTrnTime(this),
            title = optional { getString("title") },
            description = optional { getString("description") },
            state = TrnState.Default,
            purpose = null,
            tags = emptyList(),
            attachments = emptyList(),
            metadata = TrnMetadata(
                recurringRuleId = optionalUUID("recurringRuleId"),
                loanId = optionalUUID("loanId"),
                loanRecordId = optionalUUID("loanRecordId"),
            ),
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = now
            ),
        )
    }
    // endregion

    // region Transfers
    private fun parseTransfers(
        json: JSONObject,
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): Either<ImportOldDataError, List<BatchTransferData>> =
        Either.catch(ImportOldDataError.Parse::Transfers) {
            val transactionsJson = json.getJSONArray("transactions")
            val transfers = mutableListOf<BatchTransferData>()

            for (i in 0 until transactionsJson.length()) {
                val trnJson = transactionsJson.getJSONObject(i)
                if (trnJson.getString("type") != "TRANSFER")
                    continue // skip non-transfers
                transfers.add(
                    trnJson.parseTransfer(
                        now = now,
                        accountsMap = accountsMap,
                        categoriesMap = categoriesMap
                    )
                )
            }
            transfers
        }

    private fun JSONObject.parseTransfer(
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): BatchTransferData {
        val oldTrnId = getString("id")
        val accountFrom = accountsMap[getString("accountId")]
            ?: error("Transfer 'From' Account with id ${getString("accountId")} not found")
        val accountTo = accountsMap[getString("toAccountId")]
            ?: error("Transfer 'To' Account with id ${getString("toAccountId")} not found")

        return BatchTransferData(
            batchId = oldTrnId,
            transfer = TransferData(
                amountFrom = Value(
                    amount = getDouble("amount"),
                    currency = accountFrom.currency,
                ),
                amountTo = Value(
                    amount = getDouble("toAmount"),
                    currency = accountTo.currency,
                ),
                accountFrom = accountFrom,
                accountTo = accountTo,
                category = categoriesMap[getString("categoryId")],
                time = parseTrnTime(this),
                title = optional { getString("title") },
                description = optional { getString("description") },
                fee = null,
                sync = Sync(
                    state = SyncState.Syncing,
                    lastUpdated = now
                ),
            )
        )
    }
    // endregion

    // region Settings
    private fun parseSettings(
        json: JSONObject
    ): Either<ImportOldDataError, SettingsData> = Either.catch(
        ImportOldDataError.Parse::Settings
    ) {
        val settingsJson = json.getJSONArray("settings")
            .getJSONObject(0)

        SettingsData(
            baseCurrency = settingsJson.getString("currency"),
            theme = when (optional { settingsJson.get("theme") }) {
                "DARK" -> Theme.Dark
                "LIGHT" -> Theme.Light
                else -> Theme.Auto
            }
        )
    }
    // endregion

    private fun parseTrnTime(
        trnJson: JSONObject
    ): TrnTime {
        fun parseDateTime(field: String): LocalDateTime? =
            optional { trnJson.getLong(field) }
                ?.let { epochMillis ->
                    Instant.ofEpochMilli(epochMillis)
                        .toLocal(timeProvider)
                }

        return parseDateTime("dateTime")?.let { dateTime ->
            TrnTime.Actual(dateTime)
        } ?: parseDateTime("dueDate")?.let { dueDate ->
            TrnTime.Due(dueDate)
        } ?: error("Couldn't parse TrnTime")
    }

    private fun JSONObject.optionalUUID(field: String): UUID? =
        optional { getString(field).toUUID() }
}