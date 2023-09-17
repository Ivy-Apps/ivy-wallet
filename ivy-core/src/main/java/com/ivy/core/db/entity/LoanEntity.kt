package com.ivy.core.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.core.datamodel.Loan
import com.ivy.core.datamodel.LoanType
import com.ivy.core.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Keep
@Serializable
@Entity(tableName = "loans")
data class LoanEntity(
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("type")
    val type: LoanType,
    @SerialName("color")
    val color: Int = 0,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("orderNum")
    val orderNum: Double = 0.0,
    @SerialName("accountId")
    @Serializable(with = KSerializerUUID::class)
    val accountId: UUID? = null,

    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): Loan = Loan(
        name = name,
        amount = amount,
        type = type,
        color = color,
        icon = icon,
        orderNum = orderNum,
        accountId = accountId,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )

    fun humanReadableType(): String {
        return if (type == LoanType.BORROW) "BORROWED" else "LENT"
    }
}
