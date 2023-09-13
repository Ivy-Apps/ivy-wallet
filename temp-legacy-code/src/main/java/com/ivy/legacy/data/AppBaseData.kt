package com.ivy.legacy.data

import com.ivy.core.data.model.Account
import com.ivy.core.data.model.Category

data class AppBaseData(
    val baseCurrency: String,
    val accounts: List<Account>,
    val categories: List<Category>
)
