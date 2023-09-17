package com.ivy.core.datamodel

import androidx.compose.runtime.Immutable
import com.ivy.core.db.entity.LoanEntity
import com.ivy.core.util.stringRes
import com.ivy.resources.R
import java.util.UUID

@Immutable
data class Loan(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val accountId: UUID? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) {
    fun toEntity(): LoanEntity = LoanEntity(
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
        return if (type == LoanType.BORROW) {
            stringRes(R.string.borrowed_uppercase)
        } else {
            stringRes(R.string.lent_uppercase)
        }
    }
}
