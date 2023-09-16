package com.ivy.wallet.domain.data

import androidx.compose.runtime.Immutable
import com.ivy.core.data.model.TransactionHistoryItem
import java.time.LocalDate

@Immutable
data class TransactionHistoryDateDivider(
    val date: LocalDate,
    val income: Double,
    val expenses: Double
) : TransactionHistoryItem
