package com.ivy.core.ui.transaction.card

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.transaction.TransactionUi

@Immutable
data class DueActions(
    val onSkip: (TransactionUi) -> Unit,
    val onPayGet: (TransactionUi) -> Unit,
)

fun dummyDueActions() = DueActions({}, {})