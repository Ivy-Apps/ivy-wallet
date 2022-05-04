package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.LoanEntity
import java.util.*

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: LoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<LoanEntity>)

    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY orderNum ASC")
    suspend fun findAll(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun findById(id: UUID): LoanEntity?

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE loans SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM loans")
    suspend fun deleteAll()

    @Query("SELECT MAX(orderNum) FROM loans")
    suspend fun findMaxOrderNum(): Double?
}