package com.ivy.wallet.ui.statistic.level1

import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false,
    val subCategoryState: SubCategoryState = SubCategoryState(),
) {
    fun totalAmount(): Double = amount + subCategoryState.subCategoryTotalAmount
    fun clearSubcategoriesAndGet(): CategoryAmount {
        return this.copy(subCategoryState = SubCategoryState())
    }

    fun getRelevantAmount() = if (subCategoryState.subCategoryListExpanded)
        amount
    else
        totalAmount()

    data class SubCategoryState(
        val subCategoriesList: List<CategoryAmount> = emptyList(),
        val subCategoryTotalAmount: Double = 0.0,
        val subCategoryListExpanded: Boolean = false,
    )
}