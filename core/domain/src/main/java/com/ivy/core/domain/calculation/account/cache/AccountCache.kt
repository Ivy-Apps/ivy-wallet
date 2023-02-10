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

fun invalidCaches(
    caches: List<AccountCache>,
    changedTransactions: Set<Transaction>
): Set<AccountCache> {
    // TODO: Check if this can be optimized
    return caches.filter { cache ->
        becomesInvalid(cache, changedTransactions)
    }.toSet()
}

private fun becomesInvalid(
    cache: AccountCache,
    transactions: Set<Transaction>
): Boolean {
    val cacheAccount = cache.accountId
    val cacheTime = cache.rawStats.newestTransaction
    return transactions.any { trn ->
        cacheAccount in affectedAccounts(trn) &&
                (trn.time is TransactionTime.Actual) && cacheTime >= trn.time.time
    }
}

private fun affectedAccounts(transaction: Transaction): Set<AccountId> =
    when (transaction) {
        is Transaction.Expense -> setOf(transaction.account)
        is Transaction.Income -> setOf(transaction.account)
        is Transaction.Transfer -> setOf(transaction.from.account, transaction.to.account)
    }