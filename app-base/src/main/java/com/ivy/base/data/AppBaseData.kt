package com.ivy.base.data

import com.ivy.data.Account
import com.ivy.data.Category

data class AppBaseData(
    val baseCurrency: String,
    val accounts: List<Account>,
    val categories: List<Category>
)