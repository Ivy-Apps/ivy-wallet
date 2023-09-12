package com.ivy.piechart

import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
