package com.ivy.data.db.dao.write

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.data.db.entity.BudgetEntity
import java.util.UUID

@Dao
interface WriteBudgetDao {
    @Upsert
    suspend fun save(value: BudgetEntity)

    @Upsert
    suspend fun saveMany(value: List<BudgetEntity>)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE budgets SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}