package com.ivy.categories

import com.ivy.data.model.Category
import com.ivy.wallet.domain.data.Reorderable

data class CategoryData(
    val category: Category,
    val monthlyBalance: Double,
    val monthlyExpenses: Double,
    val monthlyIncome: Double
) : Reorderable {
    override fun getItemOrderNum() = category.orderNum

    override fun withNewOrderNum(newOrderNum: Double) = this.copy(
        category = category.copy(
            orderNum = newOrderNum
        )
    )
}
