package com.ivy.data.db.dao.read

import androidx.room.Dao
import androidx.room.Query
import com.ivy.data.db.entity.LoanEntity
import java.util.*

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAll(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun findById(id: UUID): LoanEntity?

    @Query("SELECT MAX(orderNum) FROM loans")
    suspend fun findMaxOrderNum(): Double?
}
