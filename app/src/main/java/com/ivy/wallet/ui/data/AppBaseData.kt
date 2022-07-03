package com.ivy.wallet.ui.data

import com.ivy.data.Account
import com.ivy.wallet.domain.data.core.Category

data class AppBaseData(
    val baseCurrency: String,
    val accounts: List<Account>,
    val categories: List<Category>
)