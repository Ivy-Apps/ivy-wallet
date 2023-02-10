package com.ivy.core.persistence.api.account

import com.ivy.core.data.AccountId
import com.ivy.core.data.calculation.AccountCache
import com.ivy.core.persistence.api.Read

interface AccountCacheRead : Read<AccountCache, AccountId, Nothing> {
    suspend fun all(): List<AccountCache>
}