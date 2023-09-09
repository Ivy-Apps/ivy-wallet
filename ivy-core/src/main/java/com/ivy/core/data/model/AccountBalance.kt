package com.ivy.core.data.model

import com.ivy.wallet.domain.data.core.Account

data class AccountBalance(
    val account: Account,
    val balance: Double
)
