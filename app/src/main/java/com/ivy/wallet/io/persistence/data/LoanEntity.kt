package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.domain.data.core.Loan
import java.util.*

@Keep
@Entity(tableName = "loans")
data class LoanEntity(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("type")
    val type: LoanType,
    @SerializedName("color")
    val color: Int = 0,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("orderNum")
    val orderNum: Double = 0.0,
    @SerializedName("accountId")
    val accountId: UUID? = null,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
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
