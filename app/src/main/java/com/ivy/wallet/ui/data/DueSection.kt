package com.ivy.wallet.ui.data

import com.ivy.data.transaction.Transaction
import com.ivy.wallet.domain.pure.data.IncomeExpensePair

data class DueSection(
    val trns: List<Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)