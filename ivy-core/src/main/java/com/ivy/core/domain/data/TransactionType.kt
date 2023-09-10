package com.ivy.wallet.domain.data

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable

@Immutable
@Keep
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}
