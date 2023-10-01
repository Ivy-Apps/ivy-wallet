package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.LoanEntity
import java.util.UUID

@Dao
interface WriteLoanDao {
    @Upsert
    suspend fun save(value: LoanEntity)

    @Upsert
    suspend fun saveMany(value: List<LoanEntity>)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE loans SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM loans")
    suspend fun deleteAll()
}