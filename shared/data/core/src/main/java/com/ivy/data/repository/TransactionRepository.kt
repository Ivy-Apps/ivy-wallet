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

    suspend fun findAll(): List<com.ivy.data.model.Transaction>

    @Suppress("FunctionNaming")
    suspend fun findAll_LIMIT_1(): List<com.ivy.data.model.Transaction>

    suspend fun findAllIncome(): List<com.ivy.data.model.Income>

    suspend fun findAllExpense(): List<com.ivy.data.model.Expense>

    suspend fun findAllTransfer(): List<com.ivy.data.model.Transfer>

    suspend fun findAllIncomeByAccount(accountId: com.ivy.data.model.AccountId): List<com.ivy.data.model.Income>

    suspend fun findAllExpenseByAccount(accountId: com.ivy.data.model.AccountId): List<com.ivy.data.model.Expense>

    suspend fun findAllTransferByAccount(accountId: com.ivy.data.model.AccountId): List<com.ivy.data.model.Transfer>

    suspend fun findAllIncomeByAccountBetween(
        accountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Income>

    suspend fun findAllExpenseByAccountBetween(
        accountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Expense>

    suspend fun findAllTransferByAccountBetween(
        accountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllTransfersToAccount(
        toAccountId: com.ivy.data.model.AccountId,
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllTransfersToAccountBetween(
        toAccountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllByAccountAndBetween(
        accountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllByCategoryAndBetween(
        categoryId: com.ivy.data.model.CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllIncomeByCategoryAndBetween(
        categoryId: com.ivy.data.model.CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Income>

    suspend fun findAllExpenseByCategoryAndBetween(
        categoryId: com.ivy.data.model.CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Expense>

    suspend fun findAllTransferByCategoryAndBetween(
        categoryId: com.ivy.data.model.CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllUnspecifiedIncomeAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Income>

    suspend fun findAllUnspecifiedExpenseAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Expense>

    suspend fun findAllUnspecifiedTransferAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllToAccountAndBetween(
        toAccountId: com.ivy.data.model.AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: com.ivy.data.model.CategoryId
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: com.ivy.data.model.AccountId
    ): List<com.ivy.data.model.Transaction>

    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<com.ivy.data.model.Transaction>

    suspend fun findAllIncomeBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<com.ivy.data.model.Income>

    suspend fun findAllExpenseBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<com.ivy.data.model.Expense>

    suspend fun findAllTransferBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<com.ivy.data.model.Transfer>

    suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<com.ivy.data.model.Transaction>

    suspend fun findById(id: com.ivy.data.model.TransactionId): com.ivy.data.model.Transaction?
    suspend fun findByIds(ids: List<com.ivy.data.model.TransactionId>): List<com.ivy.data.model.Transaction>

    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<com.ivy.data.model.Transaction>

    suspend fun countHappenedTransactions(): Long

    suspend fun findAllByTitleMatchingPattern(pattern: String): List<com.ivy.data.model.Transaction>

    suspend fun countByTitleMatchingPattern(
        pattern: String,
    ): Long

    suspend fun findAllByCategory(
        categoryId: com.ivy.data.model.CategoryId,
    ): List<com.ivy.data.model.Transaction>

    suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: com.ivy.data.model.CategoryId
    ): Long

    suspend fun findAllByAccount(
        accountId: com.ivy.data.model.AccountId
    ): List<com.ivy.data.model.Transaction>

    suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: com.ivy.data.model.AccountId
    ): Long

    suspend fun findLoanTransaction(
        loanId: UUID
    ): com.ivy.data.model.Transaction?

    suspend fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): com.ivy.data.model.Transaction?

    suspend fun findAllByLoanId(
        loanId: UUID
    ): List<com.ivy.data.model.Transaction>

    suspend fun save(accountId: com.ivy.data.model.AccountId, value: com.ivy.data.model.Transaction)

    suspend fun saveMany(accountId: com.ivy.data.model.AccountId, value: List<com.ivy.data.model.Transaction>)

    suspend fun flagDeleted(id: com.ivy.data.model.TransactionId)

    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)

    suspend fun flagDeletedByAccountId(accountId: com.ivy.data.model.AccountId)

    suspend fun deleteById(id: com.ivy.data.model.TransactionId)

    suspend fun deleteAllByAccountId(accountId: com.ivy.data.model.AccountId)

    suspend fun deleteAll()
}