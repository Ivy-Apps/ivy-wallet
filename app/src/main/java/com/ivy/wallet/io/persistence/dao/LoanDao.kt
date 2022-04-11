package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.domain.data.entity.Loan
import java.util.*

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: Loan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<Loan>)

    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY orderNum ASC")
    fun findAll(): List<Loan>

    @Query("SELECT * FROM loans WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<Loan>

    @Query("SELECT * FROM loans WHERE id = :id")
    fun findById(id: UUID): Loan?

    @Query("DELETE FROM loans WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE loans SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM loans")
    fun deleteAll()

    @Query("SELECT MAX(orderNum) FROM loans")
    fun findMaxOrderNum(): Double
}