package com.ivy.core.data.db.entity

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable

@Immutable
@Keep
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}
