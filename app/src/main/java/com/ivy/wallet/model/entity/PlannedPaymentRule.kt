package com.ivy.wallet.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.model.IntervalType
import com.ivy.wallet.model.TransactionType
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "planned_payment_rules")
data class PlannedPaymentRule(
    val startDate: LocalDateTime?,
    val intervalN: Int?,
    val intervalType: IntervalType?,
    val oneTime: Boolean,

    val type: TransactionType,
    val accountId: UUID,
    val amount: Double = 0.0,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
)