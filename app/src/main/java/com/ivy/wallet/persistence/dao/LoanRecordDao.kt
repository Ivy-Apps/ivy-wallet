package com.ivy.wallet.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.model.entity.LoanRecord
import java.util.*

@Dao
interface LoanRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: LoanRecord)

    @Query("SELECT * FROM loan_records WHERE isDeleted = 0 ORDER BY dateTime DESC")
    fun findAll(): List<LoanRecord>

    @Query("SELECT * FROM loan_records WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<LoanRecord>

    @Query("SELECT * FROM loan_records WHERE id = :id")
    fun findById(id: UUID): LoanRecord?

    @Query("SELECT * FROM loan_records WHERE loanId = :loanId AND isDeleted = 0")
    fun findAllByLoanId(loanId: UUID): List<LoanRecord>

    @Query("DELETE FROM loan_records WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("UPDATE loan_records SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("DELETE FROM loan_records")
    fun deleteAll()
}