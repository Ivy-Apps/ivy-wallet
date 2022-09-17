package com.ivy.data.transaction

import com.ivy.data.Value

data class UpcomingSection(
    val income: Value,
    val expense: Value,
    val trns: List<Transaction>,
)