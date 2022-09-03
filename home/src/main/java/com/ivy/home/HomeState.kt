package com.ivy.home

import com.ivy.data.time.SelectedPeriod
import com.ivy.data.transaction.TransactionsList

data class HomeState(
    val name: String,
    val period: SelectedPeriod?,
    val trnsList: TransactionsList,
    val balance: Double,
    val income: Double,
    val expense: Double,
    val hideBalance: Boolean
)