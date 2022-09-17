package com.ivy.data.transaction

import com.ivy.data.Value

data class OverdueSection(
    val income: Value,
    val expense: Value,
    val trns: List<Transaction>
)