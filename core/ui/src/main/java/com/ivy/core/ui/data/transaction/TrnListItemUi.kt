package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi

@Deprecated("old")
@Immutable
sealed interface TrnListItemUi {
    @Deprecated("old")
    @Immutable
    data class Trn(val trn: TransactionUi) : TrnListItemUi

    @Deprecated("old")
    @Immutable
    data class Transfer(
        val batchId: String,
        val time: TrnTimeUi,
        val from: TransactionUi,
        val to: TransactionUi,
        val fee: TransactionUi?
    ) : TrnListItemUi

    @Deprecated("old")
    @Immutable
    data class DateDivider(
        val id: String,
        val date: String,
        val day: String,
        val cashflow: ValueUi,
        val positiveCashflow: Boolean,
        val collapsed: Boolean,
    ) : TrnListItemUi
}

