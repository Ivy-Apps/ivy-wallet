package com.ivy.base

import com.ivy.data.AccountOld

data class AccountBalance(
    val account: AccountOld,
    val balance: Double
)