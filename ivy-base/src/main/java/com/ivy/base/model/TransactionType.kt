package com.ivy.base.model

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Keep
@Serializable
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}
