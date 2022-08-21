package com.ivy.data.transaction

sealed class TrnListItem {
    data class Trn(val trn: Transaction) : TrnListItem()
    data class Divider(val divider: DateDivider) : TrnListItem()
    data class UpcomingSection(val income: Value, val expense: Value) : TrnListItem()
    data class OverdueSection(val income: Value, val expense: Value) : TrnListItem()
}