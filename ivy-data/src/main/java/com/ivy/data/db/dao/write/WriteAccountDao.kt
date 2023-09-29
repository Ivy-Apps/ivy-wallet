package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.AccountEntity
import java.util.UUID

@Dao
interface WriteAccountDao {
    @Upsert
    suspend fun save(value: AccountEntity)

    @Upsert
    suspend fun saveMany(values: List<AccountEntity>)

    @Query("UPDATE accounts SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}