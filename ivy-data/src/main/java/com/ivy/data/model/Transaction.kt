package com.ivy.data.model

import com.ivy.base.model.TransactionType
import java.time.LocalDateTime
import java.util.UUID

data class Transaction(
    val id: UUID,
    val accountId: UUID,
    val type: TransactionType,
    val amount: Double,
    val toAccountId: UUID?,
    val toAmount: Double,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val categoryId: UUID,
    val dueDate: LocalDateTime,

    val recurringRuleId: UUID,


    // This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,

    // This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID,

    val isSynced: Boolean,
    val isDeleted: Boolean,
)