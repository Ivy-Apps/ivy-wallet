package com.ivy.data.repository

import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import java.time.LocalDateTime

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

    suspend fun save(value: Transaction)
    suspend fun saveMany(value: List<Transaction>)

    suspend fun flagDeleted(id: TransactionId)
    suspend fun deleteById(id: TransactionId)
    suspend fun deleteAllByAccountId(accountId: AccountId)
    suspend fun deleteAll()
}