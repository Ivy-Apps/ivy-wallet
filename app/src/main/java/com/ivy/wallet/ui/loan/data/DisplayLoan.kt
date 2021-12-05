package com.ivy.wallet.ui.loan.data

import com.ivy.wallet.model.Reorderable
import com.ivy.wallet.model.entity.Loan

data class DisplayLoan(
    val loan: Loan,
    val amountPaid: Double
) : Reorderable {
    override fun getItemOrderNum(): Double {
        return loan.orderNum
    }

    override fun withNewOrderNum(newOrderNum: Double): Reorderable {
        return this.copy(
            loan = loan.copy(
                orderNum = newOrderNum
            )
        )
    }
}