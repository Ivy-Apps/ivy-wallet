package com.ivy.wallet.ui.budget.model

import com.ivy.wallet.domain.data.Reorderable
import com.ivy.wallet.domain.data.core.Budget

data class DisplayBudget(
    val budget: Budget,
    val spentAmount: Double
) : Reorderable {
    override fun getItemOrderNum(): Double {
        return budget.orderId
    }

    override fun withNewOrderNum(newOrderNum: Double): Reorderable {
        return this.copy(
            budget = budget.copy(
                orderId = newOrderNum
            )
        )
    }
}