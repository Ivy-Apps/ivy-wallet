package com.ivy.core.data.db.write

import arrow.core.Either
import com.ivy.core.data.db.entity.TransactionEntity
import com.ivy.core.data.db.write.dao.WriteTransactionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class TransactionWriter @Inject constructor(
    private val dao: WriteTransactionDao,
) {
    suspend fun save(value: TransactionEntity): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.save(value)
            Either.Right(Unit)
        }
    }

    suspend fun saveMany(values: List<TransactionEntity>): Either<String, Unit> {
        return withContext(Dispatchers.IO) {
            dao.saveMany(values)
            Either.Right(Unit)
        }
    }

    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        withContext(Dispatchers.IO) {
            dao.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    suspend fun flagDeleted(id: UUID) {
        withContext(Dispatchers.IO) {
            dao.flagDeleted(id)
        }
    }

    suspend fun flagDeletedByAccountId(accountId: UUID) {
        withContext(Dispatchers.IO) {
            dao.flagDeletedByAccountId(accountId)
        }
    }
}