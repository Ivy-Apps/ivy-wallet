package com.ivy.wallet.io.persistence.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.PlannedPaymentRule
import java.time.LocalDateTime
import java.util.*

@Keep
@Entity(tableName = "planned_payment_rules")
data class PlannedPaymentRuleEntity(
    @SerializedName("startDate")
    val startDate: LocalDateTime?,
    @SerializedName("intervalN")
    val intervalN: Int?,
    @SerializedName("intervalType")
    val intervalType: IntervalType?,
    @SerializedName("oneTime")
    val oneTime: Boolean,

    @SerializedName("type")
    val type: TransactionType,
    @SerializedName("accountId")
    val accountId: UUID,
    @SerializedName("amount")
    val amount: Double = 0.0,
    @SerializedName("categoryId")
    val categoryId: UUID? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,

    @SerializedName("isSynced")
    val isSynced: Boolean = false,
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,

    @PrimaryKey
    @SerializedName("id")
    val id: UUID = UUID.randomUUID()
) {
    fun toDomain(): PlannedPaymentRule = PlannedPaymentRule(
        startDate = startDate,
        intervalN = intervalN,
        intervalType = intervalType,
        oneTime = oneTime,
        type = type,
        accountId = accountId,
        amount = amount,
        categoryId = categoryId,
        title = title,
        description = description,
        isSynced = isSynced,
        isDeleted = isDeleted,
        id = id
    )
}
