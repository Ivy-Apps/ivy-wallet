package com.ivy.data.transaction

import java.time.LocalDate

sealed class TrnListItem {
    data class Trn(val trn: Transaction) : TrnListItem()
    data class DateDivider(
        val date: LocalDate,
        val cashflow: Value,
    ) : TrnListItem()

    data class UpcomingSection(val income: Value, val expense: Value) : TrnListItem()
    data class OverdueSection(val income: Value, val expense: Value) : TrnListItem()
}