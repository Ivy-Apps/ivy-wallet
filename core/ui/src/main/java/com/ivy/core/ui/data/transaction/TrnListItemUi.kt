package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi

@Immutable
sealed interface TrnListItemUi {
    @Immutable
    data class Trn(val trn: TransactionUi) : TrnListItemUi

    @Immutable
    data class Transfer(
        val batchId: String,
        val time: TrnTimeUi,
        val from: TransactionUi,
        val to: TransactionUi,
        val fee: TransactionUi?
    ) : TrnListItemUi

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

