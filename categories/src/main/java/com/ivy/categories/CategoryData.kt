package com.ivy.categories

import com.ivy.base.Reorderable
import com.ivy.data.CategoryOld

data class CategoryData(
    val category: CategoryOld,
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