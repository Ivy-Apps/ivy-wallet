package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import java.time.LocalDateTime
import java.util.*

@Deprecated("use `:core:persistence`")
@Entity(tableName = "transactions")
data class TransactionEntity(
    val accountId: UUID,
    val type: TrnTypeOld,
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
    fun toDomain(): TransactionOld = TransactionOld(
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

fun TransactionOld.toEntity(): TransactionEntity = TransactionEntity(
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

    isSynced = true,
    isDeleted = false
)