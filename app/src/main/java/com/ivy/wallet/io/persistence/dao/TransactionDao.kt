package com.ivy.wallet.io.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.time.LocalDateTime
import java.util.*

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(value: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY dateTime DESC, dueDate ASC")
    fun findAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 LIMIT 1")
    fun findAll_LIMIT_1(): List<TransactionEntity>


    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type ORDER BY dateTime DESC")
    fun findAllByType(type: TransactionType): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId ORDER BY dateTime DESC")
    fun findAllByTypeAndAccount(type: TransactionType, accountId: UUID): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and accountId = :accountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllByTypeAndAccountBetween(
        type: TransactionType,
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId ORDER BY dateTime DESC")
    fun findAllTransfersToAccount(
        toAccountId: UUID,
        type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type and toAccountId = :toAccountId and dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllTransfersToAccountBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllByAccountAndBetween(
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId IS NULL) AND type = :type AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND toAccountId = :toAccountId AND dateTime >= :startDate AND dateTime <= :endDate ORDER BY dateTime DESC")
    fun findAllToAccountAndBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate ORDER BY dueDate ASC")
    fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId = :categoryId) ORDER BY dateTime DESC, dueDate ASC")
    fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND (categoryId IS NULL) ORDER BY dateTime DESC, dueDate ASC")
    fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dueDate >= :startDate AND dueDate <= :endDate AND accountId = :accountId ORDER BY dateTime DESC, dueDate ASC")
    fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC")
    fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND type = :type ORDER BY dateTime DESC")
    fun findAllBetweenAndType(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TransactionType
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND dateTime >= :startDate AND dateTime <= :endDate AND recurringRuleId = :recurringRuleId ORDER BY dateTime DESC")
    fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun findById(id: UUID): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isSynced = :synced AND isDeleted = :deleted")
    fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<TransactionEntity>

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE id = :id")
    fun flagDeleted(id: UUID)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE recurringRuleId = :recurringRuleId AND dateTime IS NULL")
    fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)

    @Query("UPDATE transactions SET isDeleted = 1, isSynced = 0 WHERE accountId = :accountId")
    fun flagDeletedByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions WHERE id = :id")
    fun deleteById(id: UUID)

    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    fun deleteAllByAccountId(accountId: UUID)

    @Query("DELETE FROM transactions")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM transactions WHERE isDeleted = 0 AND dateTime IS NOT null")
    fun countHappenedTransactions(): Long

    //Smart Title Suggestions
    @Query("SELECT * FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND isDeleted = 0")
    fun countByTitleMatchingPattern(
        pattern: String,
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND (categoryId = :categoryId) ORDER BY dateTime DESC")
    fun findAllByCategory(
        categoryId: UUID,
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND categoryId = :categoryId AND isDeleted = 0")
    fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND accountId = :accountId ORDER BY dateTime DESC")
    fun findAllByAccount(
        accountId: UUID
    ): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE title LIKE :pattern AND accountId = :accountId AND isDeleted = 0")
    fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: UUID
    ): Long

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId AND loanRecordId IS NULL")
    fun findLoanTransaction(
        loanId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanRecordId = :loanRecordId")
    fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND loanId = :loanId")
    fun findAllByLoanId(
        loanId: UUID
    ): List<TransactionEntity>
}