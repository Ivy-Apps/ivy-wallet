package com.ivy.data.repository

import com.ivy.base.model.TransactionType
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.NonNegativeLong
import java.time.LocalDateTime
import java.util.UUID

interface TransactionRepository {
    suspend fun findById(id: TransactionId): Transaction?
    suspend fun findByIds(ids: List<TransactionId>): List<Transaction>

    suspend fun findAll(): List<Transaction>
    suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income>
    suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense>
    suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer>
    suspend fun findAllTransfersToAccount(toAccountId: AccountId): List<Transfer>

    suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

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

    suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID,
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllByCategoryAndBetween(
        categoryId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>

    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction>
    suspend fun flagDeletedByAccountId(accountId: UUID)

    suspend fun save(value: Transaction)
    suspend fun saveMany(value: List<Transaction>)

    suspend fun flagDeleted(id: TransactionId)
    suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)
    suspend fun deleteById(id: TransactionId)
    suspend fun deleteAllByAccountId(accountId: AccountId)
    suspend fun deleteAll()

    suspend fun countHappenedTransactions(): NonNegativeLong
    suspend fun findLoanTransaction(
        loanId: UUID
    ): Transaction?

    suspend fun findLoanRecordTransaction(
        loanRecordId: UUID
    ): Transaction?

    suspend fun findAllByLoanId(
        loanId: UUID
    ): List<Transaction>
}