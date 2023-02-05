package com.ivy.core.persistence.api.transaction

import com.ivy.core.data.AccountId
import com.ivy.core.data.optimized.LedgerEntry
import com.ivy.core.persistence.api.Read
import java.time.LocalDateTime

interface LedgerRead : Read<LedgerEntry, Nothing, LedgerQuery> {

}

sealed interface LedgerQuery {
    data class ForAccount(
        val accountId: AccountId
    ) : LedgerQuery

    data class ForAccountAfter(
        val accountId: AccountId,
        val after: LocalDateTime
    ) : LedgerQuery
}