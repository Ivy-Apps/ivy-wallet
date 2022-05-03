package com.ivy.wallet.ui.statistic.level1

import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)