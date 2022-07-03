package com.ivy.wallet.domain.data.core

import com.ivy.data.transaction.Transaction
import com.ivy.wallet.io.network.data.TransactionDTO
import com.ivy.wallet.io.persistence.data.TransactionEntity

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    accountId = accountId,
    type = type,
    amount = amount.toDouble(),
    toAccountId = toAccountId,
    toAmount = toAmount.toDouble(),
    title = title,
    description = description,
    dateTime = dateTime,
    categoryId = categoryId,
    dueDate = dueDate,
    recurringRuleId = recurringRuleId,
    attachmentUrl = attachmentUrl,
    loanId = loanId,
    loanRecordId = loanRecordId,
    id = id,
    isSynced = isSynced,
    isDeleted = isDeleted
)

fun Transaction.toDTO(): TransactionDTO = TransactionDTO(
    accountId = accountId,
    type = type,
    amount = amount.toDouble(),
    toAccountId = toAccountId,
    toAmount = toAmount.toDouble(),
    title = title,
    description = description,
    dateTime = dateTime,
    categoryId = categoryId,
    dueDate = dueDate,
    recurringRuleId = recurringRuleId,
    attachmentUrl = attachmentUrl,
    loanId = loanId,
    loanRecordId = loanRecordId,
    id = id
)