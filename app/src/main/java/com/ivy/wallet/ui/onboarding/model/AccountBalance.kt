package com.ivy.wallet.ui.onboarding.model

import com.ivy.wallet.domain.data.core.Account

data class AccountBalance(
    val account: Account,
    val balance: Double
)