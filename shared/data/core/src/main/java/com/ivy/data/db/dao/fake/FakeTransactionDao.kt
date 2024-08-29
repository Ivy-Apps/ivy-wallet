package com.ivy.data.db.dao.fake

import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import org.jetbrains.annotations.VisibleForTesting
import java.time.Instant
import java.util.UUID

@VisibleForTesting
class FakeTransactionDao : TransactionDao, WriteTransactionDao {
    private val items = mutableListOf<TransactionEntity>()

    override suspend fun findAll(): List<TransactionEntity> {
        return items
            .filter { !it.isDeleted }
            .sortedWith(
                compareByDescending<TransactionEntity> { it.dateTime }
                    .then(compareBy { it.dueDate })
            )
    }

    override suspend fun findAll_LIMIT_1(): List<TransactionEntity> {
        return findAll().take(1)
    }

    override suspend fun findAllByType(type: TransactionType): List<TransactionEntity> {
        return items.filter { !it.isDeleted && it.type == type }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByTypeAndAccount(
        type: TransactionType,
        accountId: UUID
    ): List<TransactionEntity> {
        return items.filter { !it.isDeleted && type == it.type && it.accountId == accountId }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByTypeAndAccountBetween(
        type: TransactionType,
        accountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && !it.isDeleted && it.type == type
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllTransfersToAccount(
        toAccountId: UUID,
        type: TransactionType
    ): List<TransactionEntity> {
        return items.filter {
            it.type == TransactionType.TRANSFER && it.toAccountId == toAccountId && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID,
        startDate: Instant,
        endDate: Instant,
        type: TransactionType
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            it.type == TransactionType.TRANSFER &&
                    it.toAccountId == toAccountId &&
                    isBetween(dateTime, startDate, endDate) &&
                    !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByAccountAndBetween(
        accountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && it.accountId == accountId && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && it.categoryId == categoryId && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllUnspecifiedAndBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && it.categoryId == null && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) &&
                    it.type == type &&
                    it.categoryId == categoryId &&
                    !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && it.type == type && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllToAccountAndBetween(
        toAccountId: UUID,
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) &&
                    it.toAccountId == toAccountId &&
                    !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllDueToBetween(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dueDate = it.dueDate ?: return@filter false
            isBetween(dueDate, startDate, endDate) && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllDueToBetweenByCategory(
        startDate: Instant,
        endDate: Instant,
        categoryId: UUID
    ): List<TransactionEntity> {
        return items.filter {
            val dueDate = it.dueDate ?: return@filter false
            isBetween(dueDate, startDate, endDate) && it.categoryId == categoryId && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: Instant,
        endDate: Instant
    ): List<TransactionEntity> {
        return items.filter {
            val dueDate = it.dueDate ?: return@filter false
            isBetween(dueDate, startDate, endDate) && it.categoryId == null && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllDueToBetweenByAccount(
        startDate: Instant,
        endDate: Instant,
        accountId: UUID
    ): List<TransactionEntity> {
        return items.filter {
            val dueDate = it.dueDate ?: return@filter false
            isBetween(dueDate, startDate, endDate) && it.accountId == accountId && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity> {
        return items.filter { it.recurringRuleId == recurringRuleId && !it.isDeleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun findAllBetweenAndType(
        startDate: Instant,
        endDate: Instant,
        type: TransactionType
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) && it.type == type && !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findAllBetweenAndRecurringRuleId(
        startDate: Instant,
        endDate: Instant,
        recurringRuleId: UUID
    ): List<TransactionEntity> {
        return items.filter {
            val dateTime = it.dateTime ?: return@filter false
            isBetween(dateTime, startDate, endDate) &&
                    it.recurringRuleId == recurringRuleId &&
                    !it.isDeleted
        }.sortedByDescending { it.dateTime }
    }

    override suspend fun findById(id: UUID): TransactionEntity? {
        return items.find { it.id == id }
    }

    override suspend fun findByIds(ids: List<UUID>): List<TransactionEntity> {
        return items.filter { it.id in ids }
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<TransactionEntity> {
        return items.filter { it.isSynced == synced && it.isDeleted == deleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun countHappenedTransactions(): Long {
        return items.count { it.dateTime != null && !it.isDeleted }.toLong()
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity> {
        return items.filter { it.title?.contains(pattern) == true && !it.isDeleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        return items.count { it.title?.contains(pattern) == true && !it.isDeleted }.toLong()
    }

    override suspend fun findAllByCategory(categoryId: UUID): List<TransactionEntity> {
        return items.filter { it.categoryId == categoryId && !it.isDeleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: UUID
    ): Long {
        return items.count {
            it.title?.contains(pattern) == true && it.categoryId == categoryId && !it.isDeleted
        }.toLong()
    }

    override suspend fun findAllByAccount(accountId: UUID): List<TransactionEntity> {
        return items.filter { it.accountId == accountId && !it.isDeleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: UUID
    ): Long {
        return items.count {
            it.title?.contains(pattern) == true && it.accountId == accountId && !it.isDeleted
        }.toLong()
    }

    override suspend fun findLoanTransaction(loanId: UUID): TransactionEntity? {
        return items.find { it.loanId == loanId }
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): TransactionEntity? {
        return items.find { it.loanRecordId == loanRecordId }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<TransactionEntity> {
        return items.filter { it.loanId == loanId && !it.isDeleted }
            .sortedByDescending { it.dateTime }
    }

    override suspend fun save(value: TransactionEntity) {
        val existingItemIndex = items.indexOfFirst { it.id == value.id }
        if (existingItemIndex > -1) {
            items[existingItemIndex] = value
        } else {
            items.add(value)
        }
    }

    override suspend fun saveMany(values: List<TransactionEntity>) {
        values.forEach { save(it) }
    }

    override suspend fun deletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        items.removeIf { it.recurringRuleId == recurringRuleId }
    }

    override suspend fun deleteById(id: UUID) {
        items.removeIf { it.id == id }
    }

    override suspend fun deleteAllByAccountId(accountId: UUID) {
        items.removeIf { it.accountId == accountId }
    }

    override suspend fun deleteAll() {
        items.clear()
    }

    private fun isBetween(
        dateTime: Instant,
        startDate: Instant,
        endDate: Instant
    ): Boolean {
        return !dateTime.isAfter(endDate) && !dateTime.isBefore(startDate)
    }
}