package com.ivy.wallet.ui.loan.data

import com.ivy.wallet.domain.data.Reorderable
import com.ivy.wallet.domain.data.core.Loan
import com.ivy.wallet.utils.getDefaultFIATCurrency

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