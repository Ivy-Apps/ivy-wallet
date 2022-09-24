package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.FormattedValue

@Immutable
data class DueSectionUi(
    val name: String,
    val income: FormattedValue,
    val expense: FormattedValue,
    val trns: List<TransactionUi>,
)