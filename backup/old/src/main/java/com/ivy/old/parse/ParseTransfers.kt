package com.ivy.old.parse

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.data.BatchTransferData
import com.ivy.backup.base.data.FaultTolerantList
import com.ivy.backup.base.maybe
import com.ivy.backup.base.parseTrnTime
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.action.transaction.transfer.TransferData
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

data class TransfersData(
    val transfers: FaultTolerantList<BatchTransferData>,
    val partlyCorrupted: List<Transaction>,
)

internal fun parseTransfers(
    json: JSONObject,
    now: LocalDateTime,
    accountsMap: Map<String, Account>,
    categoriesMap: Map<String, Category>,
    timeProvider: TimeProvider,
): Either<ImportBackupError.Parse, TransfersData> =
    Either.catch(ImportBackupError.Parse::Transfers) {
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
                categoriesMap = categoriesMap,
                timeProvider = timeProvider,
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
    timeProvider: TimeProvider,
): Either<Transaction, BatchTransferData>? = maybe {
    val oldTrnId = getString("id")
    val accountFrom = accountsMap[getString("accountId")]
    val accountTo = accountsMap[getString("toAccountId")]

    val fromAmount = getDouble("amount")
    val toAmount = maybe { getDouble("toAmount") } ?: fromAmount

    val category = maybe { categoriesMap[getString("categoryId")] }
    val title = maybe { getString("title") }
    val description = maybe { getString("description") }
    val trnTime = parseTrnTime(this, timeProvider = timeProvider)
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