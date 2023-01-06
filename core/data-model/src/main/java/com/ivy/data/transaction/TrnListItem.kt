package com.ivy.data.transaction

import com.ivy.data.Value
import java.time.LocalDate

sealed interface TrnListItem {
    data class Trn(val trn: Transaction) : TrnListItem

    data class Transfer(
        val batchId: String,
        val time: TrnTime,
        val from: Transaction,
        val to: Transaction,
        val fee: Transaction?
    ) : TrnListItem

    data class DateDivider(
        val id: String,
        val date: LocalDate,
        val cashflow: Value,
        val collapsed: Boolean,
    ) : TrnListItem
}

