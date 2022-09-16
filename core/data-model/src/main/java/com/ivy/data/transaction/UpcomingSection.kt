package com.ivy.data.transaction

data class UpcomingSection(
    val income: Value,
    val expense: Value,
    val trns: List<Transaction>,
)