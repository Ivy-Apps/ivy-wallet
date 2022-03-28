package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.Budget
import java.util.*

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: Budget)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<Budget>)

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY orderId ASC")
    fun findAll(): List<Budget>

    @Query("SELECT * FROM budgets WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<Budget>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun findById(id: UUID): Budget?

    @Query("DELETE FROM budgets WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE budgets SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM budgets")
    fun deleteAll()

    @Query("SELECT MAX(orderId) FROM budgets")
    fun findMaxOrderNum(): Double
}