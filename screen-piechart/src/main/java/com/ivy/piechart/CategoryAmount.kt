package com.ivy.piechart

import androidx.compose.runtime.Immutable
import com.ivy.legacy.datamodel.Category

@Immutable
data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: List<Transaction> = emptyList(),
    val isCategoryUnspecified: Boolean = false
)
