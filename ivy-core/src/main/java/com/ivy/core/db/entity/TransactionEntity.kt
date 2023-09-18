package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.Transaction
import com.ivy.core.kotlinxserilzation.KSerializerLocalDateTime
import com.ivy.core.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Keep
@Serializable
@Entity(tableName = "transactions")
data class TransactionEntity(
    @SerialName("accountId")
    @Serializable(with = KSerializerUUID::class)
    val accountId: UUID,
    @SerialName("type")
    val type: TransactionType,
    @SerialName("amount")
    val amount: Double,
    @SerialName("toAccountId")
    @Serializable(with = KSerializerUUID::class)
    val toAccountId: UUID? = null,
    @SerialName("toAmount")
    val toAmount: Double? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("dateTime")
    @Serializable(with = KSerializerLocalDateTime::class)
    val dateTime: LocalDateTime? = null,
    @SerialName("categoryId")
    @Serializable(with = KSerializerUUID::class)
    val categoryId: UUID? = null,
    @SerialName("dueDate")
    @Serializable(with = KSerializerLocalDateTime::class)
    val dueDate: LocalDateTime? = null,
    @SerialName("recurringRuleId")
    @Serializable(with = KSerializerUUID::class)
    val recurringRuleId: UUID? = null,
    @SerialName("attachmentUrl")
    val attachmentUrl: String? = null,
    // This refers to the loan id that is linked with a transaction
    @SerialName("loanId")
    @Serializable(with = KSerializerUUID::class)
    val loanId: UUID? = null,
    // This refers to the loan record id that is linked with a transaction
    @SerialName("loanRecordId")
    @Serializable(with = KSerializerUUID::class)
    val loanRecordId: UUID? = null,
    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
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
