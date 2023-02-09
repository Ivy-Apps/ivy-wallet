package com.ivy.core.domain.api.action.read

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.data.AccountId
import com.ivy.core.data.calculation.AccountCache
import com.ivy.core.data.calculation.RawStats
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.calculation.account.accountRawStats
import com.ivy.core.domain.calculation.account.cache.accountRawStatsWithCache
import com.ivy.core.persistence.api.account.AccountCacheRead
import com.ivy.core.persistence.api.account.AccountCacheWrite
import com.ivy.core.persistence.api.transaction.LedgerQuery
import com.ivy.core.persistence.api.transaction.LedgerRead
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AccountRawStatsFlow @Inject constructor(
    private val ledgerRead: LedgerRead,
    private val accountCacheRead: AccountCacheRead,
    private val accountCacheWrite: AccountCacheWrite,
    private val timeProvider: TimeProvider,
) : FlowAction<AccountId, RawStats>() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(input: AccountId): Flow<RawStats> =
        accountCacheRead.single(input).flatMapLatest { cache ->
            if (cache != null) calculateWithCache(cache) else calculateWithoutCache(input)
        }

    private fun calculateWithCache(cache: AccountCache): Flow<RawStats> = ledgerRead.many(
        LedgerQuery.ForAccountAfter(cache.accountId, cache.rawStats.newestTransaction)
    ).map { entriesAfterCache ->
        accountRawStatsWithCache(cache, entriesAfterCache)
    }.updateAccountCache(cache.accountId)

    private fun calculateWithoutCache(accountId: AccountId): Flow<RawStats> = ledgerRead.many(
        LedgerQuery.ForAccount(accountId)
    ).map { allEntries ->
        accountRawStats(accountId, allEntries)
    }.updateAccountCache(accountId)

    private fun Flow<RawStats>.updateAccountCache(accountId: AccountId) =
        onEach { rawStats ->
            val accountCache = AccountCache(
                accountId = accountId,
                rawStats = rawStats,
            )
            accountCacheWrite.save(accountCache)
        }
}