package com.ivy.data.repository

import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import java.time.LocalDateTime
import java.util.UUID

interface TransactionRepository {

    suspend fun findAll(): List<Transaction>

    suspend fun findAllIncome(): List<Income>

    suspend fun findAllExpense(): List<Expense>

    suspend fun findAllTransfer(): List<Transfer>

    suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income>

    suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense>

    suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer>

    suspend fun findAllIncomeByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income>

    suspend fun findAllExpenseByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense>

    suspend fun findAllTransferByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer>

    suspend fun findAllTransfersToAccount(
        toAccountId: AccountId,
    ): List<Transfer>

    suspend fun findAllTransfersToAccountBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Transfer>

    suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllIncomeByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income>

    suspend fun findAllExpenseByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense>

    suspend fun findAllTransferByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer>

    suspend fun findAllUnspecifiedIncomeAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income>

    suspend fun findAllUnspecifiedExpenseAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense>

    suspend fun findAllUnspecifiedTransferAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer>

    suspend fun findAllToAccountAndBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: CategoryId
    ): List<Transaction>

    suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Transaction>

    suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: AccountId
    ): List<Transaction>

    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction>

    suspend fun findAllIncomeBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Income>

    suspend fun findAllExpenseBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Expense>

    suspend fun findAllTransferBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Transfer>

    suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<Transaction>

    suspend fun findById(id: TransactionId): Transaction?
    suspend fun findByIds(ids: List<TransactionId>): List<Transaction>

    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<Transaction>

    suspend fun countHappenedTransactions(): Long

    suspend fun findAllByTitleMatchingPattern(pattern: String): List<Transaction>

    suspend fun countByTitleMatchingPattern(
        pattern: String,
    ): Long

    suspend fun findAllByCategory(
        categoryId: CategoryId,
    ): List<Transaction>

    suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: CategoryId
    ): Long

    suspend fun findAllByAccount(
        accountId: AccountId
    ): List<Transaction>

    suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: AccountId
    ): Long

    suspend fun findLoanTransaction(
        loanId: UUID
    ): Transaction?

    suspend fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): Transaction?

    suspend fun findAllByLoanId(
        loanId: UUID
    ): List<Transaction>

    suspend fun save(accountId: AccountId, value: Transaction)

    suspend fun saveMany(accountId: AccountId, value: List<Transaction>)

    suspend fun flagDeleted(id: TransactionId)

    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)

    suspend fun flagDeletedByAccountId(accountId: AccountId)

    suspend fun deleteById(id: TransactionId)

    suspend fun deleteAllByAccountId(accountId: AccountId)

    suspend fun deleteAll()
}