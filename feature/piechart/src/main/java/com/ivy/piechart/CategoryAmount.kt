package com.ivy.piechart

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.Transaction
import com.ivy.data.model.Category

@Immutable
data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
