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
    fun save(value: LoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<LoanEntity>)

    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY orderNum ASC")
    fun findAll(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :id")
    fun findById(id: UUID): LoanEntity?

    @Query("DELETE FROM loans WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE loans SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM loans")
    fun deleteAll()

    @Query("SELECT MAX(orderNum) FROM loans")
    fun findMaxOrderNum(): Double
}