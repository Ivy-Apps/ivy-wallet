package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import java.time.LocalDateTime
import java.util.*

@Keep
@Entity(tableName = "transactions")
data class TransactionEntity(
    @SerializedName("accountId")
    val accountId: UUID,
    @SerializedName("type")
    val type: TransactionType,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("toAccountId")
    val toAccountId: UUID? = null,
    @SerializedName("toAmount")
    val toAmount: Double? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("dateTime")
    val dateTime: LocalDateTime? = null,
    @SerializedName("categoryId")
    val categoryId: UUID? = null,
    @SerializedName("dueDate")
    val dueDate: LocalDateTime? = null,

    @SerializedName("recurringRuleId")
    val recurringRuleId: UUID? = null,

    @SerializedName("attachmentUrl")
    val attachmentUrl: String? = null,

    // This refers to the loan id that is linked with a transaction
    @SerializedName("loanId")
    val loanId: UUID? = null,

    // This refers to the loan record id that is linked with a transaction
    @SerializedName("loanRecordId")
    val loanRecordId: UUID? = null,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
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

        // Set isSynced && isDeleted to false so they aren't accounted in the equals check
        return this.copy(
            isSynced = false,
            isDeleted = false
        ) == transaction.copy(
            isSynced = false,
            isDeleted = false
        )
    }
}
