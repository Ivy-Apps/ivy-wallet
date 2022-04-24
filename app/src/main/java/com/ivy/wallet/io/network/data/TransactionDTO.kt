package com.ivy.wallet.io.network.data

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.time.LocalDateTime
import java.util.*

data class TransactionDTO(
    val accountId: UUID,
    val type: TransactionType,
    val amount: Double,
    val toAccountId: UUID? = null,
    val toAmount: Double? = null,
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
) {
    fun toEntity(): TransactionEntity = TransactionEntity(
        accountId = accountId,
        type = type,
        amount = amount,
        toAccountId = toAccountId,
        toAmount = toAmount,
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

        isSynced = true,
        isDeleted = false
    )
}