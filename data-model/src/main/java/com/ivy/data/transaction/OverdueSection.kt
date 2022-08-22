package com.ivy.data.transaction

data class OverdueSection(
    val income: Value,
    val expense: Value,
    val trns: List<Transaction>
)