package com.ivy.core.domain.calculation.account

import com.ivy.core.data.AccountId
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.domain.calculation.rawStats
import com.ivy.core.domain.data.RawStats

/**
 * @param entries all entries for the account including transfers (from - to)
 */
fun accountRawStats(
    account: AccountId,
    entries: List<LedgerEntry>
): RawStats = rawStats(
    entries,
    interpretTransfer = {
        val transferEntries = mutableListOf<LedgerEntry.Single>()
        if (it.from.account == account) {
            // transfer going out of the account
            transferEntries.add(LedgerEntry.Single.Expense(it.from.value, it.time))
        }
        if (it.to.account == account) {
            // transfer going in the account
            transferEntries.add(LedgerEntry.Single.Income(it.to.value, it.time))
        }
        transferEntries
    }
)