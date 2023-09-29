package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.TransactionEntity
import java.util.UUID

@Dao
interface WriteTransactionDao {
    @Upsert
    suspend fun save(value: TransactionEntity)

    @Upsert
    suspend fun saveMany(value: List<TransactionEntity>)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query(
        "UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE" +
                " recurringRuleId = :recurringRuleId AND dateTime IS NULL"
    )
    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    suspend fun flagDeletedByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteAllByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}