package com.ivy.sync.transaction

import com.ivy.common.mapToTrnType
import com.ivy.data.SyncMetadata
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.wallet.io.network.data.TransactionDTO

fun Transaction.mark(
    isSynced: Boolean,
    isDeleted: Boolean
): Transaction = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = isDeleted,
        )
    )
)

fun mapToDTO(trn: Transaction): TransactionDTO = TransactionDTO(
    accountId = trn.account.id,
    type = mapToTrnType(trn.type),
    amount = trn.amount,
    toAccountId = (trn.type as? TransactionType.Transfer)?.toAccount?.id,
    toAmount = (trn.type as? TransactionType.Transfer)?.toAmount,
    title = trn.title,
    description = trn.description,
    dateTime = (trn.time as? TrnTime.Actual)?.actual,
    categoryId = trn.category?.id,
    dueDate = (trn.time as? TrnTime.Due)?.due,
    recurringRuleId = trn.metadata.recurringRuleId,
    attachmentUrl = trn.attachmentUrl,
    loanId = trn.metadata.loanId,
    loanRecordId = trn.metadata.loanRecordId,
    id = trn.id,
)