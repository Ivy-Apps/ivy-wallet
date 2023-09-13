package com.ivy.legacy.data

import com.ivy.core.data.model.Transaction
import com.ivy.wallet.domain.pure.data.IncomeExpensePair

data class DueSection(
    val trns: List<Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)
