package com.ivy.core.domain.calculation.account

import com.ivy.core.data.AccountId
import com.ivy.core.data.calculation.RawStats
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.domain.calculation.rawStats

/**
 * @param entries all entries for the account including transfers (from - to)
 */
fun accountRawStats(
    account: AccountId,
    entries: List<LedgerEntry>
): RawStats = rawStats(
    entries
) { (from, to, time) ->
    buildList {
        if (from.account == account) {
            // transfer going out of the account
            // are interpreted as expenses
            add(LedgerEntry.Single.Expense(from.value, time))
        }
        if (to.account == account) {
            // transfer going in the account
            // are interpreted as incomes
            add(LedgerEntry.Single.Income(to.value, time))
        }
    }
}