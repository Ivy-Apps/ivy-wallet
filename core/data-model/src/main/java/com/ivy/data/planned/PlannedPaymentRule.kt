package com.ivy.data.planned

import com.ivy.data.transaction.TrnTypeOld
import java.time.LocalDateTime
import java.util.*

data class PlannedPaymentRule(
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

    val id: UUID = UUID.randomUUID()
)