package com.ivy.wallet.ui.statistic.level1

import com.ivy.wallet.domain.data.entity.Category
import com.ivy.wallet.domain.data.entity.Transaction

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList()
)