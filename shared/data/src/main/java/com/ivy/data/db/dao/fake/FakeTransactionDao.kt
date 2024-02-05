package com.ivy.data.db.dao.fake

import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import org.jetbrains.annotations.VisibleForTesting
import java.time.LocalDateTime
import java.util.UUID

@VisibleForTesting
class FakeTransactionDao : TransactionDao, WriteTransactionDao {
    private val items = mutableListOf<TransactionEntity>()

    // SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY dateTime DESC, dueDate ASC"
    override suspend fun findAll(): List<TransactionEntity> {
        return items
            .filter { !it.isDeleted }
            .sortedWith(
                compareByDescending<TransactionEntity> { it.dateTime }
                    .then(compareBy { it.dueDate })
            )
    }

    // SELECT * FROM transactions WHERE isDeleted = 0 LIMIT 1
    override suspend fun findAll_LIMIT_1(): List<TransactionEntity> {
        return findAll().take(1)
    }

    // SELECT * FROM transactions WHERE isDeleted = 0 AND type = :type ORDER BY dateTime DESC
    override suspend fun findAllByType(type: TransactionType): List<TransactionEntity> {
        return items.filter { !it.isDeleted && it.type == type }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByTypeAndAccount(
        type: TransactionType,
        accountId: UUID
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByTypeAndAccountBetween(
        type: TransactionType,
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllTransfersToAccount(
        toAccountId: UUID,
        type: TransactionType
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TransactionType
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByAccountAndBetween(
        accountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllToAccountAndBetween(
        toAccountId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: UUID
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: UUID
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllBetweenAndType(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        type: TransactionType
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): TransactionEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun countHappenedTransactions(): Long {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByCategory(categoryId: UUID): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: UUID
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByAccount(accountId: UUID): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: UUID
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun findLoanTransaction(loanId: UUID): TransactionEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): TransactionEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun save(value: TransactionEntity) {
        items.add(value)
    }

    override suspend fun saveMany(values: List<TransactionEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun flagDeleted(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun flagDeletedByAccountId(accountId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAllByAccountId(accountId: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        items.clear()
    }
}