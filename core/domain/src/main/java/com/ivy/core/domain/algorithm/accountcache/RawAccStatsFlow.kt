package com.ivy.core.domain.algorithm.accountcache

import arrow.core.None
import arrow.core.Some
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.plus
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.persistence.IvyWalletCoreDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Account-Cache algo:
 * https://github.com/Ivy-Apps/ivy-wallet/blob/develop/docs/algorithms/Account-Cache%20Algo.md
 */
class RawAccStatsFlow @Inject constructor(
    private val db: IvyWalletCoreDb
) : FlowAction<String, RawStats>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createFlow(accountId: String): Flow<RawStats> {
        val accountCacheFlow = db.accountCacheDao().findAccountCache(accountId)

        return accountCacheFlow.flatMapLatest { cache ->
            when (val cachedStats = cache?.let(::accountCacheToRawStats)) {
                is Some -> withCache(
                    accountId = accountId,
                    cachedStats = cachedStats.value,
                )
                null, None -> fromScratch(accountId)
            }
        }
    }

    private fun withCache(
        accountId: String,
        cachedStats: RawStats,
    ): Flow<RawStats> = db.calcTrnDao().findActualByAccountAfter(
        accountId = accountId,
        timestamp = cachedStats.newestTrnTime
    ).map { newerTrns ->
        if (newerTrns.isEmpty()) {
            // No new transactions, the result will be the cache value
            return@map cachedStats
        }

        val newerStats = rawStats(newerTrns)
        val result = cachedStats + newerStats

        updateCache(accountId, result)

        result
    }

    private fun fromScratch(
        accountId: String
    ): Flow<RawStats> = db.calcTrnDao()
        .findAllActualByAccount(accountId)
        .map { trns ->
            val result = rawStats(trns)
            updateCache(accountId, result)
            result
        }

    private suspend fun updateCache(accountId: String, stats: RawStats) {
        db.accountCacheDao().save(
            rawStatsToAccountCache(accountId, stats)
        )
    }
}

