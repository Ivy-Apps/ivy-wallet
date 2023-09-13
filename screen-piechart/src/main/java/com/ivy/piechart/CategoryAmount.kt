package com.ivy.piechart

import com.ivy.core.data.model.Category
import com.ivy.core.data.model.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
