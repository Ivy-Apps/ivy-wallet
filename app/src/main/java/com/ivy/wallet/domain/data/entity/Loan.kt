package com.ivy.wallet.domain.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.stringRes
import java.util.*

@Entity(tableName = "loans")
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

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) {
    fun humanReadableType(): String {
        return if (type == LoanType.BORROW) stringRes(R.string.borrowed_uppercase) else stringRes(
                    R.string.lent_uppercase)
    }
}