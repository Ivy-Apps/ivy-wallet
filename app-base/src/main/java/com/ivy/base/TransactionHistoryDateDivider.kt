package com.ivy.base

import java.time.LocalDate

@Deprecated("use DateDivider")
data class TransactionHistoryDateDivider(
    val date: LocalDate,
    val income: Double,
    val expenses: Double
)