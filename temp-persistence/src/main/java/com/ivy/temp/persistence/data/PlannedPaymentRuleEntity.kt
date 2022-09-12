package com.ivy.wallet.io.persistence.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.data.planned.IntervalType
import com.ivy.data.planned.PlannedPaymentRule
import com.ivy.data.transaction.TrnTypeOld
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "planned_payment_rules")
data class PlannedPaymentRuleEntity(
    val startDate: LocalDateTime?,
    val intervalN: Int?,
    val intervalType: IntervalType?,
    val oneTime: Boolean,

    val type: TrnTypeOld,
    val accountId: UUID,
    val amount: Double = 0.0,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
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

fun PlannedPaymentRule.toEntity(): PlannedPaymentRuleEntity = PlannedPaymentRuleEntity(
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
