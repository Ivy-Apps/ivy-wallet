package com.ivy.data.model

import com.ivy.base.model.TransactionType
import java.time.LocalDateTime
import java.util.UUID

@JvmInline
value class TransactionId(val value: UUID)

data class Transaction(
    val id: TransactionId,
    val accountId: AccountId,
    val type: TransactionType,
    val amount: Double,
    val toAccountId: AccountId?,
    val toAmount: Double,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val categoryId: CategoryId?,
    val dueDate: LocalDateTime,

    val recurringRuleId: UUID,


    // This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,

    // This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID,

    val isSynced: Boolean,
    val isDeleted: Boolean,
)