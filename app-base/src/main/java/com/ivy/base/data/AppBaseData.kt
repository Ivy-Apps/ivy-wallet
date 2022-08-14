package com.ivy.base.data

import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld

data class AppBaseData(
    val baseCurrency: String,
    val accounts: List<AccountOld>,
    val categories: List<CategoryOld>
)