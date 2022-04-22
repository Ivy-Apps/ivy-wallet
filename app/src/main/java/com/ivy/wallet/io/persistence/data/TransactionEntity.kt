package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "transactions")
data class TransactionEntity(
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

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Transaction = Transaction(
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
        recurringRuleId = recurringRuleId,
        attachmentUrl = attachmentUrl,
        loanId = loanId,
        loanRecordId = loanRecordId,
        id = id
    )

    fun isIdenticalWith(transaction: TransactionEntity?): Boolean {
        if (transaction == null) return false

        //Set isSynced && isDeleted to false so they aren't accounted in the equals check
        return this.copy(
            isSynced = false,
            isDeleted = false
        ) == transaction.copy(
            isSynced = false,
            isDeleted = false
        )
    }
}