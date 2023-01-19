package com.ivy.data.transaction

import com.ivy.data.Value
import java.time.LocalDate

@Deprecated("don't use - use the one `algorithms`")
sealed interface TrnListItem {
    @Deprecated("don't use - use the one `algorithms`")
    data class Trn(val trn: Transaction) : TrnListItem

    // TODO: Extract as type
    data class Transfer(
        val batchId: String,
        val time: TrnTime,
        val from: Transaction,
        val to: Transaction,
        val fee: Transaction?
    ) : TrnListItem

    @Deprecated("don't use - use the one `algorithms`")
    data class DateDivider(
        val id: String,
        val date: LocalDate,
        val cashflow: Value,
        val collapsed: Boolean,
    ) : TrnListItem
}

