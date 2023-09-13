package com.ivy.wallet.domain.data

import com.ivy.core.data.model.TransactionHistoryItem
import java.time.LocalDate

data class TransactionHistoryDateDivider(
    val date: LocalDate,
    val income: Double,
    val expenses: Double
) : TransactionHistoryItem
