package com.ivy.wallet.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.Reorderable
import java.util.*

@Entity(tableName = "loans")
data class Loan(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Int = 0,
    val icon: String? = null,
    val orderNum: Double = 0.0,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    @PrimaryKey
    val id: UUID = UUID.randomUUID()
) : Reorderable {
    override fun getItemOrderNum() = orderNum

    override fun withNewOrderNum(newOrderNum: Double) = this.copy(
        orderNum = newOrderNum
    )
}