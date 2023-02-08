package com.ivy.core.persistence.api.transaction

import com.ivy.core.data.Transaction
import com.ivy.core.data.TransactionId
import com.ivy.core.data.common.TimeRange
import com.ivy.core.persistence.api.Read

interface TransactionRead : Read<Transaction, TransactionId, TransactionRead.Query> {
    sealed interface Query {
        data class ForPeriod(val range: TimeRange) : Query
    }
}