package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable

@Immutable
data class TransactionsListUi(
    val upcoming: DueSectionUi?,
    val overdue: DueSectionUi?,
    val history: List<TrnListItemUi>,
)