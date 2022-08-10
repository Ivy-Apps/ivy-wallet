package com.ivy.base.data

import com.ivy.data.pure.IncomeExpensePair
import com.ivy.data.transaction.TransactionOld

data class DueSection(
    val trns: List<TransactionOld>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)