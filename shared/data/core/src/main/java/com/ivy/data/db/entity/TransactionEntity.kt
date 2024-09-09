package com.ivy.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.base.kotlinxserilzation.KSerializerInstant
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import com.ivy.base.model.TransactionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Suppress("DataClassDefaultValues")
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
    @Serializable(with = KSerializerInstant::class)
    val dateTime: Instant? = null,
    @SerialName("categoryId")
    @Serializable(with = KSerializerUUID::class)
    val categoryId: UUID? = null,
    @SerialName("dueDate")
    @Serializable(with = KSerializerInstant::class)
    val dueDate: Instant? = null,
    @SerialName("recurringRuleId")
    @Serializable(with = KSerializerUUID::class)
    val recurringRuleId: UUID? = null,
    @SerialName("paidForDateTime")
    @Serializable(with = KSerializerInstant::class)
    val paidForDateTime: Instant? = null,
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
    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
)
