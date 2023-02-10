package com.ivy.core.domain.calculation.account.cache

import com.ivy.core.data.AccountId
import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionTime
import com.ivy.core.data.calculation.AccountCache
import com.ivy.core.data.calculation.RawStats
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.domain.calculation.account.accountRawStats
import com.ivy.core.domain.calculation.plus

fun accountRawStatsWithCache(
    cache: AccountCache,
    entriesAfterCache: List<LedgerEntry>
): RawStats = cache.rawStats + accountRawStats(cache.accountId, entriesAfterCache)

fun cachesToInvalidate(
    old: Transaction?,
    new: Transaction,
    caches: List<AccountCache>
): Set<AccountCache> {
    if (
        (old == null || old.time is TransactionTime.Due) &&
        new.time is TransactionTime.Due
    ) return emptySet() // due transactions don't affect the cache

    return caches.filter {
        becomesInvalid(it, old, new)
    }.toSet()
}

private fun becomesInvalid(
    cache: AccountCache,
    old: Transaction?,
    new: Transaction,
): Boolean {
    val cacheAccount = cache.accountId
    if (cacheAccount !in affectedAccounts(old) && cacheAccount !in affectedAccounts(new))
        return false // no affected => it's still valid

    val cacheTime = cache.rawStats.newestTransaction
    return (old?.time == null || cacheTime < old.time.time) && cacheTime < new.time.time
}

private fun affectedAccounts(transaction: Transaction?): Set<AccountId> = when (transaction) {
    is Transaction.Expense -> setOf(transaction.account)
    is Transaction.Income -> setOf(transaction.account)
    is Transaction.Transfer -> setOf(transaction.from.account, transaction.to.account)
    null -> emptySet()
}