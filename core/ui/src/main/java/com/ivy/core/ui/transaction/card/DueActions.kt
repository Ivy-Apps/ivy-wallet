package com.ivy.core.ui.transaction.card

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.transaction.TransactionUi
import com.ivy.core.ui.data.transaction.TrnListItemUi

@Immutable
data class DueActions(
    val onSkipTrn: (TransactionUi) -> Unit,
    val onExecuteTrn: (TransactionUi) -> Unit,
    val onSkipTransfer: (TrnListItemUi.Transfer) -> Unit,
    val onExecuteTransfer: (TrnListItemUi.Transfer) -> Unit,
)

fun dummyDueActions() = DueActions({}, {}, {}, {})