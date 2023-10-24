package com.ivy.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.base.legacy.Transaction
import com.ivy.data.db.entity.TransactionEntity
import javax.inject.Inject

class TransactionMapper @Inject constructor() {

    fun TransactionEntity.toDomain(): Either<String, Transaction> = either {
        Transaction(
            accountId = accountId,
            type = type,
            amount = amount.toBigDecimal(),
            toAccountId = toAccountId,
            toAmount = toAmount?.toBigDecimal() ?: amount.toBigDecimal(),
            title = title,
            description = description,
            dateTime = dateTime,
            categoryId = categoryId,
            dueDate = dueDate,
            date = dateTime?.toLocalDate(),
            time = dateTime?.toLocalTime(),
            recurringRuleId = recurringRuleId,
            attachmentUrl = attachmentUrl,
            loanId = loanId,
            loanRecordId = loanRecordId,
            isSynced = isSynced,
            isDeleted = isDeleted,
            id = id
        )
    }

    fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
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
            isSynced = isSynced,
            isDeleted = isDeleted,
            id = id
        )
    }
}