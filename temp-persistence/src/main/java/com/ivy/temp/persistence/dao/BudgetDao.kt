package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.BudgetEntity
import java.util.*

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<BudgetEntity>)

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY orderId ASC")
    suspend fun findAll(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun findById(id: UUID): BudgetEntity?

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE budgets SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()

    @Query("SELECT MAX(orderId) FROM budgets")
    suspend fun findMaxOrderNum(): Double?
}