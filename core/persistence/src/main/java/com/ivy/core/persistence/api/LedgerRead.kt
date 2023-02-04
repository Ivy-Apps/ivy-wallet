package com.ivy.core.persistence.api

import com.ivy.core.data.AccountId
import com.ivy.core.data.optimized.LedgerEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface LedgerRead {
    fun entries(query: QueryEntries): Flow<List<LedgerEntry>>

    sealed interface QueryEntries {
        data class ForAccount(
            val accountId: AccountId
        ) : QueryEntries

        data class ForAccountAfter(
            val accountId: AccountId,
            val after: LocalDateTime
        ) : QueryEntries
    }
}
