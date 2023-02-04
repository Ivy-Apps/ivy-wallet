package com.ivy.core.persistence.api.recurring

import com.ivy.core.data.TimeRange
import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionId
import com.ivy.core.persistence.api.Read

interface DueTransactionRead : Read<Transaction, TransactionId, DueTransactionRead.Query> {
    sealed interface Query {
        data class ForPeriod(val range: TimeRange) : Query
    }
}