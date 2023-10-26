package com.ivy.data.testing

import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.repository.TransactionRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class FakeTransactionRepository : TransactionRepository {
    private val transactionsMap = mutableMapOf<AccountId, List<Transaction>>()
    override suspend fun findAll(): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter { !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun findAll_LIMIT_1(): List<Transaction> {
        return transactionsMap.values.flatten().take(1)
    }

    override suspend fun findAllIncome(): List<Income> {
        return transactionsMap.values
            .filterIsInstance<Income>()
            .filter { !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllExpense(): List<Expense> {
        return transactionsMap.values
            .filterIsInstance<Expense>()
            .filter { !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllTransfer(): List<Transfer> {
        return transactionsMap.values
            .filterIsInstance<Transfer>()
            .filter { !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income> {
        return transactionsMap[accountId]?.filterIsInstance<Income>()
            ?.filter { !it.removed }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense> {
        return transactionsMap[accountId]?.filterIsInstance<Expense>()
            ?.filter { !it.removed }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer> {
        return transactionsMap[accountId]?.filterIsInstance<Transfer>()
            ?.filter { !it.removed }
            ?.sortedByDescending { it.time }
            .orEmpty()

    }

    override suspend fun findAllIncomeByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return transactionsMap[accountId]?.filterIsInstance<Income>()
            ?.filter {
                val incomeLocalDateTime = toLocalDateTime(it.time)
                isBetween(incomeLocalDateTime, startDate, endDate) && !it.removed
            }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllExpenseByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return transactionsMap[accountId]?.filterIsInstance<Expense>()
            ?.filter {
                val expenseLocalDateTime = toLocalDateTime(it.time)
                isBetween(expenseLocalDateTime, startDate, endDate) && !it.removed
            }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllTransferByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return transactionsMap[accountId]?.filterIsInstance<Transfer>()
            ?.filter {
                val transferLocalDateTime = toLocalDateTime(it.time)
                isBetween(transferLocalDateTime, startDate, endDate) && !it.removed
            }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllTransfersToAccount(toAccountId: AccountId): List<Transfer> {
        return transactionsMap.values
            .flatten()
            .filterIsInstance<Transfer>()
            .filter { it.toAccount == toAccountId && !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return transactionsMap.values
            .flatten()
            .filterIsInstance<Transfer>()
            .filter {
                val transferLocalDateTime = toLocalDateTime(it.time)
                it.toAccount == toAccountId
                        && isBetween(transferLocalDateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return transactionsMap[accountId]?.filter {
            val dateTime = toLocalDateTime(it.time)
            isBetween(dateTime, startDate, endDate) && !it.removed
        }
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun findAllByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == categoryId && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == null && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllIncomeByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return transactionsMap.values
            .filterIsInstance<Income>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == categoryId && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllExpenseByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return transactionsMap.values
            .filterIsInstance<Expense>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == categoryId && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllTransferByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return transactionsMap.values
            .filterIsInstance<Transfer>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == categoryId && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllUnspecifiedIncomeAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return transactionsMap.values
            .filterIsInstance<Income>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == null && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllUnspecifiedExpenseAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return transactionsMap.values
            .filterIsInstance<Expense>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == null && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllUnspecifiedTransferAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return transactionsMap.values
            .filterIsInstance<Transfer>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.category == null && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllToAccountAndBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return emptyList()
    }

    override suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return emptyList()
    }

    override suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: CategoryId
    ): List<Transaction> {
        return emptyList()
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return emptyList()
    }

    override suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: AccountId
    ): List<Transaction> {
        return emptyList()
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter {
                it.metadata.recurringRuleId == recurringRuleId && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllIncomeBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return transactionsMap.values
            .filterIsInstance<Income>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllExpenseBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return transactionsMap.values
            .filterIsInstance<Expense>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllTransferBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return transactionsMap.values
            .filterIsInstance<Transfer>()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter {
                val dateTime = toLocalDateTime(it.time)
                it.metadata.recurringRuleId == recurringRuleId
                        && isBetween(dateTime, startDate, endDate) && !it.removed
            }
            .sortedByDescending { it.time }
    }

    override suspend fun findById(id: TransactionId): Transaction? {
        return transactionsMap.values
            .flatten()
            .find { transaction -> transaction.id == id }
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter { it.removed == deleted }
            .sortedByDescending { it.time }
    }

    override suspend fun countHappenedTransactions(): Long {
        return transactionsMap.values
            .flatten()
            .count { !it.removed }
            .toLong()
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter { transaction ->
                transaction.title?.let { pattern in it.value && !transaction.removed } ?: false
            }
            .sortedByDescending { it.time }
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        return transactionsMap.values
            .flatten()
            .count { transaction ->
                transaction.title?.let { pattern in it.value && !transaction.removed } ?: false
            }
            .toLong()
    }

    override suspend fun findAllByCategory(categoryId: CategoryId): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter { it.category == categoryId && !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: CategoryId
    ): Long {
        return transactionsMap.values
            .flatten()
            .count { transaction ->
                transaction.title?.let {
                    pattern in it.value && transaction.category == categoryId && !transaction.removed
                } ?: false
            }.toLong()
    }

    override suspend fun findAllByAccount(accountId: AccountId): List<Transaction> {
        return transactionsMap[accountId]
            ?.sortedByDescending { it.time }
            .orEmpty()
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: AccountId
    ): Long {
        return transactionsMap[accountId]?.count { transaction ->
            transaction.title?.let {
                pattern in it.value && !transaction.removed
            } ?: false
        }?.toLong() ?: 0L
    }

    override suspend fun findLoanTransaction(loanId: UUID): Transaction? {
        return transactionsMap.values
            .flatten()
            .find { it.metadata.loanId == loanId && !it.removed }
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): Transaction? {
        return transactionsMap.values
            .flatten()
            .find { it.metadata.loanRecordId == loanRecordId && !it.removed }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<Transaction> {
        return transactionsMap.values
            .flatten()
            .filter { it.metadata.loanId == loanId && !it.removed }
            .sortedByDescending { it.time }
    }

    override suspend fun save(accountId: AccountId, value: Transaction) {
        transactionsMap[accountId] = transactionsMap[accountId]?.map {
            if (it.id == value.id) value
            else it
        }.orEmpty()
    }

    override suspend fun saveMany(accountId: AccountId, value: List<Transaction>) {
        value.forEach { save(accountId, it) }
    }

    override suspend fun flagDeleted(id: TransactionId) {
        for (entry in transactionsMap) {
            var found = false
            val modifiedTransactionForEntry = entry.value.map { transaction ->
                if (transaction.id == id) {
                    found = true
                    when (transaction) {
                        is Expense -> transaction.copy(removed = true)
                        is Income -> transaction.copy(removed = true)
                        is Transfer -> transaction.copy(removed = true)
                    }
                } else transaction
            }
            transactionsMap[entry.key] = modifiedTransactionForEntry

            if (found) return
        }
    }

    override suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        for (entry in transactionsMap) {
            var found = false
            val modifiedTransactionForEntry = entry.value.map { transaction ->
                if (transaction.metadata.recurringRuleId == recurringRuleId) {
                    found = true
                    when (transaction) {
                        is Expense -> transaction.copy(removed = true)
                        is Income -> transaction.copy(removed = true)
                        is Transfer -> transaction.copy(removed = true)
                    }
                } else transaction
            }
            transactionsMap[entry.key] = modifiedTransactionForEntry

            if (found) return
        }
    }

    override suspend fun flagDeletedByAccountId(accountId: AccountId) {
        transactionsMap[accountId] = transactionsMap[accountId]?.map { transaction ->
            when (transaction) {
                is Expense -> transaction.copy(removed = true)
                is Income -> transaction.copy(removed = true)
                is Transfer -> transaction.copy(removed = true)
            }
        }.orEmpty()
    }

    override suspend fun deleteById(id: TransactionId) {
        for (entry in transactionsMap) {
            var found = false
            val filteredTransactionsForEntry = entry.value.filter { transaction ->
                if (transaction.id == id) {
                    found = true
                    false
                } else true
            }
            transactionsMap[entry.key] = filteredTransactionsForEntry

            if (found) return
        }
    }

    override suspend fun deleteAllByAccountId(accountId: AccountId) {
        transactionsMap[accountId] = emptyList()
    }

    override suspend fun deleteAll() {
        transactionsMap.replaceAll { _, _ -> emptyList() }
    }

    private fun toLocalDateTime(instant: Instant) =
        instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

    private fun isBetween(
        dateTime: LocalDateTime,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Boolean {
        return dateTime.isAfter(startDate) && dateTime.isBefore(endDate)
    }
}