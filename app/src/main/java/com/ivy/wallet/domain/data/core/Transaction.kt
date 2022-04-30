package com.ivy.wallet.domain.data.core

import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.io.network.data.TransactionDTO
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    //TODO: Remove default values & introduce Transaction#dummy() method
    val accountId: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val toAccountId: UUID? = null,
    val toAmount: BigDecimal = amount,
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

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,


    val id: UUID = UUID.randomUUID()
) : TransactionHistoryItem {
    fun toEntity(): TransactionEntity = TransactionEntity(
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

    fun toDTO(): TransactionDTO = TransactionDTO(
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
}