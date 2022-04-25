package com.ivy.wallet.domain.data.core

import com.ivy.wallet.R
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.io.network.data.LoanDTO
import com.ivy.wallet.io.persistence.data.LoanEntity
import com.ivy.wallet.stringRes
import java.util.*

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

    fun toDTO(): LoanDTO = LoanDTO(
        name = name,
        amount = amount,
        type = type,
        color = color,
        icon = icon,
        orderNum = orderNum,
        accountId = accountId,
        id = id
    )

    fun humanReadableType(): String {
        return if (type == LoanType.BORROW) stringRes(R.string.borrowed_uppercase) else stringRes(
            R.string.lent_uppercase)
    }
}