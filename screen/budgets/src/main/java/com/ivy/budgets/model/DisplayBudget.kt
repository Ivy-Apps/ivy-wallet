package com.ivy.budgets.model

import androidx.compose.runtime.Immutable
import com.ivy.legacy.datamodel.Budget
import com.ivy.wallet.domain.data.Reorderable

@Immutable
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
