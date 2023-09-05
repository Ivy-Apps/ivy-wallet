package com.ivy.wallet.ui.statistic.level1

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.utils.emptyImmutableList

data class CategoryAmount(
    val category: Category?,
    val amount: Double,
    val associatedTransactions: ImmutableList<Transaction> = emptyImmutableList(),
    val isCategoryUnspecified: Boolean = false
)
