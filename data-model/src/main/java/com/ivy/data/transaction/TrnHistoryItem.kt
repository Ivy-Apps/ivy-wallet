package com.ivy.data.transaction

sealed class TrnHistoryItem {
    data class Trn(val trn: Transaction) : TrnHistoryItem()
    data class Divider(val divider: DateDivider) : TrnHistoryItem()
}