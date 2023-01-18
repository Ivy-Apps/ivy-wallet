package com.ivy.core.domain.algorithm.accountcache

import arrow.core.NonEmptyList
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.data.transaction.TrnTime
import java.time.Instant
import javax.inject.Inject

/**
 * Account-Cache algo:
 * https://github.com/Ivy-Apps/ivy-wallet/blob/develop/docs/algorithms/Account-Cache%20Algo.md
 */
class InvalidateAccCacheAct @Inject constructor(
    private val db: IvyWalletCoreDb,
    private val timeProvider: TimeProvider,
) : Action<InvalidateAccCacheAct.Input, Unit>() {
    sealed interface Input {
        /**
         * Account ids of the caches that might be affected
         */
        val accountIds: NonEmptyList<String>

        data class OnDeleteAcc(override val accountIds: NonEmptyList<String>) : Input
        data class OnDeleteTrn(
            val time: TrnTime,
            override val accountIds: NonEmptyList<String>
        ) : Input

        data class OnCreateTrn(
            val time: TrnTime,
            override val accountIds: NonEmptyList<String>
        ) : Input

        data class OnUpdateTrn(
            val oldTime: TrnTime,
            val time: TrnTime,
            override val accountIds: NonEmptyList<String>
        ) : Input

        data class Invalidate(override val accountIds: NonEmptyList<String>) : Input
    }

    override suspend fun action(input: Input) {
        when (input) {
            is Input.OnDeleteAcc -> {
                invalidateCache(input.accountIds.head)
            }
            is Input.Invalidate -> {
                input.accountIds.forEach { invalidateCache(it) }
            }
            else -> input.accountIds.forEach {
                ensureCacheConsistency(it, input)
            }
        }
    }

    private suspend fun ensureCacheConsistency(accountId: String, input: Input) {
        val cacheTime = db.accountCacheDao().findTimestampById(accountId)
        if (cacheTime != null) {
            val (oldTime, time) = when (input) {
                is Input.OnCreateTrn -> null to input.time
                is Input.OnDeleteTrn -> null to input.time
                is Input.OnUpdateTrn -> input.oldTime to input.time
                else -> error("Impossible!")
            }

            if (!cacheValid(cacheTime, oldTime, time)) {
                invalidateCache(accountId)
            }
        }
    }

    private fun cacheValid(
        cacheTime: Instant,
        oldTime: TrnTime?,
        time: TrnTime
    ): Boolean {
        fun trnIsAfterCache(): Boolean {
            val actual = time.actualTime()
            return actual != null && actual > cacheTime
        }

        /**
         * Handles the case of changing trns times from the past (before cache) to the future
         */
        fun wasNotBeforeCache(): Boolean {
            val oldActual = oldTime?.actualTime()
            return oldActual != null && oldActual > cacheTime
        }

        return trnIsAfterCache() && wasNotBeforeCache()
    }

    private fun TrnTime.actualTime(): Instant? = when (this) {
        is TrnTime.Actual -> actual.toUtc(timeProvider)
        is TrnTime.Due -> null // due transactions doesn't affect raw stats
    }

    private suspend fun invalidateCache(accountId: String) {
        db.accountCacheDao().delete(accountId)
    }
}