package com.ivy.temp.persistence

import com.ivy.common.mapToTrnType
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.wallet.io.persistence.data.TransactionEntity


fun mapToEntity(trn: Transaction): TransactionEntity = TransactionEntity(
    id = trn.id,
    accountId = trn.account.id,
    type = mapToTrnType(trn.type),
    amount = trn.value.amount,
    toAccountId = (trn.type as? TransactionType.Transfer)?.toAccount?.id,
    toAmount = (trn.type as? TransactionType.Transfer)?.toValue?.amount,
    title = trn.title,
    description = trn.description,
    dateTime = (trn.time as? TrnTime.Actual)?.actual,
    categoryId = trn.category?.id,
    dueDate = (trn.time as? TrnTime.Due)?.due,
    recurringRuleId = trn.metadata.recurringRuleId,
    attachmentUrl = trn.attachmentUrl,
    loanId = trn.metadata.loanId,
    loanRecordId = trn.metadata.loanRecordId,
    isSynced = trn.metadata.sync.isSynced,
    isDeleted = trn.metadata.sync.isDeleted
)