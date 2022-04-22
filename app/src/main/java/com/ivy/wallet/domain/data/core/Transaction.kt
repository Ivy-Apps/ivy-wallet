package com.ivy.wallet.domain.data.core

import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    val accountId: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val toAccountId: UUID? = null,
    val toAmount: BigDecimal,
    val title: String? = null,
    val description: String? = null,
    val dateTime: LocalDateTime? = null,
    val categoryId: UUID? = null,
    val dueDate: LocalDateTime? = null,

    val recurringRuleId: UUID? = null,

    val attachmentUrl: String? = null,

    //This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,

    //This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID? = null,

    val id: UUID = UUID.randomUUID()
) : TransactionHistoryItem