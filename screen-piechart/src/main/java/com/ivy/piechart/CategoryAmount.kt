package com.ivy.piechart

import com.ivy.core.datamodel.Category
import com.ivy.core.datamodel.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
