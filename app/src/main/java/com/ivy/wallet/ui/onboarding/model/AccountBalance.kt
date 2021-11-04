package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.model.entity.Account

data class AccountBalance(
    val account: Account,
    val balance: Double
)