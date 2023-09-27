package com.ivy.loans.loan.data

import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.wallet.domain.data.Reorderable

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
