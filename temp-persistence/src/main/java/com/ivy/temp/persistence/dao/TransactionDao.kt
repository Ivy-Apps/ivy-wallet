package com.ivy.wallet.io.persistence.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.time.LocalDateTime
import java.util.*

@Deprecated("use `:core:persistence`")
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(value: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY dateTime DESC, dueDate ASC")
    suspend fun findAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 LIMIT 1")
    suspend fun findAll_LIMIT_1(): List<TransactionEntity>


    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type ORDER BY dateTime DESC")
    suspend fun findAllByType(type: TrnTypeOld): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId ORDER BY dateTime DESC")
    suspend fun findAllByTypeAndAccount(
        type: TrnTypeOld,
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllByTypeAndAccountBetween(
        type: TrnTypeOld,
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId ORDER BY dateTime DESC")
    suspend fun findAllTransfersToAccount(
        toAccountId: UUID,
        type: TrnTypeOld = TrnTypeOld.TRANSFER
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TrnTypeOld = TrnTypeOld.TRANSFER
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllByAccountAndBetween(
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TrnTypeOld,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TrnTypeOld,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND toAccountId = :toAccountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    suspend fun findAllToAccountAndBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC")
    suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId = :categoryId) ORDER BY dateTime DESC, dueDate ASC")
    suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId IS NULL) ORDER BY dateTime DESC, dueDate ASC")
    suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND accountId = :accountId ORDER BY dateTime DESC, dueDate ASC")
    suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC")
    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND type = :type ORDER BY dateTime DESC")
    suspend fun findAllBetweenAndType(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TrnTypeOld
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC")
    suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun findById(id: UUID): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isSynced = :synced AND isDeleted = :deleted")
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<TransactionEntity>

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    suspend fun flagDeleted(id: UUID)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE recurringRuleId = :recurringRuleId AND dateTime IS NULL")
    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    suspend fun flagDeletedByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteAllByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM transactions WHERE isDeleted = 0 AND dateTime IS NOT null")
    suspend fun countHappenedTransactions(): Long

    //Smart Title Suggestions
    @Query("SELECT * FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    suspend fun countByTitleMatchingPattern(
        pattern: String,
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) ORDER BY dateTime DESC")
    suspend fun findAllByCategory(
        categoryId: UUID,
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND categoryId = :categoryId AND isDeleted = 0")
    suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId ORDER BY dateTime DESC")
    suspend fun findAllByAccount(
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND accountId = :accountId AND isDeleted = 0")
    suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId AND loanRecordId IS NULL")
    suspend fun findLoanTransaction(
        loanId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanRecordId = :loanRecordId")
    suspend fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId")
    suspend fun findAllByLoanId(
        loanId: UUID
    ): List<TransactionEntity>

    @RawQuery
    suspend fun findByQuery(query: SupportSQLiteQuery): List<TransactionEntity>
}