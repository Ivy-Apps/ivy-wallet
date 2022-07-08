package com.ivy.base.data

import com.ivy.data.pure.IncomeExpensePair
import com.ivy.data.transaction.Transaction

data class DueSection(
    val trns: List<Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)