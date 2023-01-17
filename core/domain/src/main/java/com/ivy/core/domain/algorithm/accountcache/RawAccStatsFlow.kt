package com.ivy.core.domain.algorithm.accountcache

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RawAccStatsFlow @Inject constructor(
    private val db: IvyWalletCoreDb
) : FlowAction<String, RawStats>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun createFlow(accountId: String): Flow<RawStats> {
        val accountCacheFlow = db.accountCacheDao().findAccountCache(accountId)

        return accountCacheFlow.flatMapLatest { cache ->
            if (cache != null) withCache(
                accountId = accountId,
                cache = cache
            ) else fromScratch()
        }
    }

    private fun withCache(
        accountId: String,
        cache: AccountCacheEntity
    ): Flow<RawStats> {
        val trnDao = db.calcTrnDao()
        trnDao.findActualByAccountAfter(
            accountId = accountId,
            timestamp = cache.timestamp
        ).map { newerTrn ->
            val newerStats = rawStats(newerTrn)

        }
        TODO()
    }

    private fun fromScratch(): Flow<RawStats> {
        TODO()
    }

}