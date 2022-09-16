package com.ivy.home

import com.ivy.data.time.SelectedPeriod
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.Value

data class HomeState(
    val name: String,
    val period: SelectedPeriod?,
    val trnsList: TransactionsList,
    val balance: Value,
    val income: Value,
    val expense: Value,
    val hideBalance: Boolean
)