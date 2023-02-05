package com.ivy.core.data.calculation

import com.ivy.core.data.AccountId

data class AccountCache(
    val accountId: AccountId,
    val rawStats: RawStats,
)