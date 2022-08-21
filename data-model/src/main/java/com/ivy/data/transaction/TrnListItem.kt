package com.ivy.data.transaction

import java.time.LocalDate

sealed class TrnListItem {
    data class Trn(val trn: Transaction) : TrnListItem()
    data class DateDivider(
        val date: LocalDate,
        val cashflow: Value,
    ) : TrnListItem()


}

