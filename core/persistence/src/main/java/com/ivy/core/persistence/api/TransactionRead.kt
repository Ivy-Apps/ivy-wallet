package com.ivy.core.persistence.api

import com.ivy.core.data.TimeRange
import com.ivy.core.data.TransactionId
import com.ivy.core.data.optimized.PartialTransaction
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRead {
    suspend fun transaction(id: TransactionId): Transaction?

    fun partialTransactions(query: QueryPartial): Flow<List<PartialTransaction>>

    sealed interface QueryPartial {
        data class ForPeriod(val range: TimeRange) : QueryPartial
    }
}