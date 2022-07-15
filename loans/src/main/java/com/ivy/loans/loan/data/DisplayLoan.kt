package com.ivy.wallet.ui.loan.data

import com.ivy.base.Reorderable
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.data.loan.Loan

data class DisplayLoan(
    val loan: Loan,
    val amountPaid: Double,
    val currencyCode: String? = getDefaultFIATCurrency().currencyCode,
    val formattedDisplayText: String = "",
    val percentPaid: Double = 0.0
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