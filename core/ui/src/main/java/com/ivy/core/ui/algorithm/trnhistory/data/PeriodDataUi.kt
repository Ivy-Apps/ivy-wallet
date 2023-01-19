package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi

@Immutable
data class PeriodDataUi(
    val periodIncome: ValueUi,
    val periodExpense: ValueUi,
    val items: List<TrnListItemUi>,
)