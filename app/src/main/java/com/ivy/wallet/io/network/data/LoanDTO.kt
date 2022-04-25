package com.ivy.wallet.io.network.data

import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.io.persistence.data.LoanEntity
import java.util.*

data class LoanDTO(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val accountId: UUID? = null,

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
        id = id,

        isSynced = true,
        isDeleted = false
    )

    fun humanReadableType(): String {
        return if (type == LoanType.BORROW) "BORROWED" else "LENT"
    }
}