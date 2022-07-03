package com.ivy.wallet.ui.onboarding.model

import com.ivy.data.Account

data class AccountBalance(
    val account: Account,
    val balance: Double
)