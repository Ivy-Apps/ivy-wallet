package com.ivy.core.domain.calculation.account.cache

import com.ivy.core.data.calculation.AccountCache
import com.ivy.core.data.calculation.RawStats
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.domain.calculation.account.accountRawStats
import com.ivy.core.domain.calculation.plus

fun accountRawStatsWithCache(
    cache: AccountCache,
    entriesAfterCache: List<LedgerEntry>
): RawStats = cache.rawStats + accountRawStats(cache.accountId, entriesAfterCache)

