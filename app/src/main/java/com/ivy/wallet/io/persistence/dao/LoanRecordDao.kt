package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.io.persistence.data.LoanRecordEntity
import java.util.*

@Dao
interface LoanRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: LoanRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<LoanRecordEntity>)

    @Query("SELECT * FROM loan_records WHERE isDeleted = 0 ORDER BY dateTime DESC")
    suspend fun findAll(): List<LoanRecordEntity>

    @Query("SELECT * FROM loan_records WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<LoanRecordEntity>

    @Query("SELECT * FROM loan_records WHERE id = :id")
    suspend fun findById(id: UUID): LoanRecordEntity?

    @Query("SELECT * FROM loan_records WHERE loanId = :loanId AND isDeleted = 0 ORDER BY dateTime DESC")
    suspend fun findAllByLoanId(loanId: UUID): List<LoanRecordEntity>

    @Query("DELETE FROM loan_records WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("UPDATE loan_records SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("DELETE FROM loan_records")
    suspend fun deleteAll()
}