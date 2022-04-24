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
    fun save(value: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<BudgetEntity>)

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY orderId ASC")
    fun findAll(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun findById(id: UUID): BudgetEntity?

    @Query("DELETE FROM budgets WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE budgets SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM budgets")
    fun deleteAll()

    @Query("SELECT MAX(orderId) FROM budgets")
    fun findMaxOrderNum(): Double
}