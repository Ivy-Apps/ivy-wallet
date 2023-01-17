package com.ivy.core.domain.algorithm.accountcache

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.plus
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.persistence.IvyWalletCoreDb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class RawAccStatsFlow @Inject constructor(
    private val db: IvyWalletCoreDb
) : FlowAction<String, RawStats>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createFlow(accountId: String): Flow<RawStats> {
        val accountCacheFlow = db.accountCacheDao().findAccountCache(accountId)

        return accountCacheFlow.flatMapLatest { cache ->
            if (cache != null) {
                val cachedRawStats = accountCacheToRawStats(cache).orNull()
                    ?: return@flatMapLatest fromScratch()
                withCache(
                    accountId = accountId,
                    cachedStats = cachedRawStats,
                    cacheTime = cache.timestamp
                )
            } else fromScratch()
        }
    }

    private fun withCache(
        accountId: String,
        cachedStats: RawStats,
        cacheTime: Instant
    ): Flow<RawStats> {
        val trnDao = db.calcTrnDao()
        trnDao.findActualByAccountAfter(
            accountId = accountId,
            timestamp = cacheTime
        ).map { newerTrns ->
            if (newerTrns.isEmpty()) {
                // No new transactions, the result will be the cache value
                return@map cacheTime
            }
            val newerStats = rawStats(newerTrns)

            val stats = cachedStats + newerStats

        }
        TODO()
    }

    private fun fromScratch(): Flow<RawStats> {
        TODO()
    }

}