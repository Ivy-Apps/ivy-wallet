package com.ivy.old.parse

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.maybe
import com.ivy.backup.base.optionalUUID
import com.ivy.backup.base.parseTrnTime
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnState
import org.json.JSONObject
import java.time.LocalDateTime

internal fun parseTransactions(
    json: JSONObject,
    now: LocalDateTime,
    accountsMap: Map<String, Account>,
    categoriesMap: Map<String, Category>,
    timeProvider: TimeProvider,
): Either<ImportBackupError.Parse, List<Transaction>> =
    Either.catch(ImportBackupError.Parse::Transactions) {
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
                    categoriesMap = categoriesMap,
                    timeProvider = timeProvider,
                )
            )
        }
        transactions
    }

private fun JSONObject.parseTransaction(
    now: LocalDateTime,
    accountsMap: Map<String, Account>,
    categoriesMap: Map<String, Category>,
    timeProvider: TimeProvider,
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
        category = maybe { categoriesMap[getString("categoryId")] },
        time = parseTrnTime(this, timeProvider = timeProvider),
        title = maybe { getString("title") },
        description = maybe { getString("description") },
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
