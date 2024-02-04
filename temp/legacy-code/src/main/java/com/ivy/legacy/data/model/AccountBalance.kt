package com.ivy.legacy.data.model

import androidx.compose.runtime.Immutable
import com.ivy.legacy.datamodel.Account

@Immutable
data class AccountBalance(
    val account: Account,
    val balance: Double
)
