package com.ivy.old

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.ivy.backup.base.data.BackupData
import com.ivy.backup.base.data.BatchTransferData
import com.ivy.backup.base.data.FaultTolerantList
import com.ivy.backup.base.data.SettingsData
import com.ivy.backup.base.optional
import com.ivy.common.time.beginningOfIvyTime
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
        val transfersData = parseTransfers(
            json = json,
            now = now,
            accountsMap = accountsMap,
            categoriesMap = categoriesMap
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
            category = optional { categoriesMap[getString("categoryId")] },
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
    private data class TransfersData(
        val transfers: FaultTolerantList<BatchTransferData>,
        val partlyCorrupted: List<Transaction>,
    )

    private fun parseTransfers(
        json: JSONObject,
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): Either<ImportOldDataError, TransfersData> =
        Either.catch(ImportOldDataError.Parse::Transfers) {
            val transactionsJson = json.getJSONArray("transactions")
            val transfers = mutableListOf<BatchTransferData>()
            val partlyCorrupted = mutableListOf<Transaction>()

            var corrupted = 0
            for (i in 0 until transactionsJson.length()) {
                val trnJson = transactionsJson.getJSONObject(i)
                if (trnJson.getString("type") != "TRANSFER")
                    continue // skip non-transfers

                val eitherTransfer = trnJson.parseTransfer(
                    now = now,
                    accountsMap = accountsMap,
                    categoriesMap = categoriesMap
                )
                if (eitherTransfer != null) {
                    when (eitherTransfer) {
                        is Either.Left -> partlyCorrupted.add(eitherTransfer.value)
                        is Either.Right -> transfers.add(eitherTransfer.value)
                    }
                } else {
                    corrupted++
                }

            }

            TransfersData(
                transfers = FaultTolerantList(items = transfers, faulty = corrupted),
                partlyCorrupted = partlyCorrupted,
            )
        }

    private fun JSONObject.parseTransfer(
        now: LocalDateTime,
        accountsMap: Map<String, Account>,
        categoriesMap: Map<String, Category>,
    ): Either<Transaction, BatchTransferData>? = optional {
        val oldTrnId = getString("id")
        val accountFrom = accountsMap[getString("accountId")]
        val accountTo = accountsMap[getString("toAccountId")]

        val fromAmount = getDouble("amount")
        val toAmount = optional { getDouble("toAmount") } ?: fromAmount

        val category = optional { categoriesMap[getString("categoryId")] }
        val title = optional { getString("title") }
        val description = optional { getString("description") }
        val trnTime = parseTrnTime(this)
        val sync = Sync(
            state = SyncState.Syncing,
            lastUpdated = now
        )

        when {
            accountFrom != null && accountTo != null -> {
                BatchTransferData(
                    batchId = oldTrnId,
                    transfer = TransferData(
                        amountFrom = Value(
                            amount = fromAmount,
                            currency = accountFrom.currency,
                        ),
                        amountTo = Value(
                            amount = toAmount,
                            currency = accountTo.currency,
                        ),
                        accountFrom = accountFrom,
                        accountTo = accountTo,
                        category = category,
                        time = trnTime,
                        title = title,
                        description = description,
                        fee = null,
                        sync = sync,
                    )
                ).right()
            }
            accountFrom != null && accountTo == null -> {
                // Expense (money sent to the void)
                Transaction(
                    id = oldTrnId.toUUID(),
                    type = TransactionType.Expense,
                    value = Value(
                        amount = fromAmount,
                        currency = accountFrom.currency,
                    ),
                    account = accountFrom,
                    title = title,
                    description = description,
                    category = category,
                    time = trnTime,
                    state = TrnState.Default,
                    purpose = null,
                    tags = emptyList(),
                    attachments = emptyList(),
                    metadata = TrnMetadata(
                        recurringRuleId = null,
                        loanId = null,
                        loanRecordId = null,
                    ),
                    sync = sync,
                ).left()
            }
            accountFrom == null && accountTo != null -> {
                // Income (money coming from the void)
                Transaction(
                    id = oldTrnId.toUUID(),
                    type = TransactionType.Income,
                    value = Value(
                        amount = toAmount,
                        currency = accountTo.currency,
                    ),
                    account = accountTo,
                    title = title,
                    description = description,
                    category = category,
                    time = trnTime,
                    state = TrnState.Default,
                    purpose = null,
                    tags = emptyList(),
                    attachments = emptyList(),
                    metadata = TrnMetadata(
                        recurringRuleId = null,
                        loanId = null,
                        loanRecordId = null,
                    ),
                    sync = sync,
                ).left()
            }
            else -> error("Corrupted transfer JSON: $this")
        }

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

        return parseDateTime("dateTime")
            ?.let(TrnTime::Actual) ?: parseDateTime("dueDate")
            ?.let(TrnTime::Due) ?: TrnTime.Actual(
            beginningOfIvyTime()
        )
    }

    private fun JSONObject.optionalUUID(field: String): UUID? =
        optional { getString(field).toUUID() }
}