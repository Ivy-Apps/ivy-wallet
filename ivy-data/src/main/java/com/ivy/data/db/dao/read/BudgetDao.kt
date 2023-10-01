package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.BudgetEntity
import java.util.*

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY orderId ASC")
    suspend fun findAll(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun findById(id: UUID): BudgetEntity?

    @Query("SELECT MAX(orderId) FROM budgets")
    suspend fun findMaxOrderNum(): Double?
}
