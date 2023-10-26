package com.ivy.data.repository.impl

import com.ivy.base.model.TransactionType
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.source.LocalTransactionDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val accountRepository: AccountRepository,
    private val mapper: TransactionMapper,
    private val dataSource: LocalTransactionDataSource,
) : TransactionRepository {
    override suspend fun findAll(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAll().mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAll_LIMIT_1(): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAll_LIMIT_1().mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncome(): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByType(TransactionType.INCOME).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpense(): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByType(TransactionType.EXPENSE).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransfer(): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByType(TransactionType.TRANSFER).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccount(TransactionType.INCOME, accountId.value).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccount(TransactionType.EXPENSE, accountId.value)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
                }
        }
    }

    override suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccount(TransactionType.TRANSFER, accountId.value)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
                }
        }
    }

    override suspend fun findAllIncomeByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccountBetween(
                type = TransactionType.INCOME,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpenseByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccountBetween(
                type = TransactionType.EXPENSE,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransferByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTypeAndAccountBetween(
                type = TransactionType.TRANSFER,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllTransfersToAccount(
        toAccountId: AccountId
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllTransfersToAccount(toAccountId.value).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllTransfersToAccountBetween(
                toAccountId = toAccountId.value,
                startDate = startDate,
                endDate = endDate,
                type = TransactionType.TRANSFER
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllBetween(startDate, endDate).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByAccountAndBetween(accountId.value, startDate, endDate).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByCategoryAndBetween(categoryId.value, startDate, endDate)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllUnspecifiedAndBetween(startDate, endDate).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncomeByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.INCOME,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpenseByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.EXPENSE,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransferByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.TRANSFER,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllUnspecifiedIncomeAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.INCOME,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllUnspecifiedExpenseAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.EXPENSE,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllUnspecifiedTransferAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.TRANSFER,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllToAccountAndBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllToAccountAndBetween(toAccountId.value, startDate, endDate)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllDueToBetween(startDate, endDate).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: CategoryId
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllDueToBetweenByCategory(startDate, endDate, categoryId.value)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllDueToBetweenByCategoryUnspecified(startDate, endDate).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: AccountId
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllDueToBetweenByAccount(startDate, endDate, accountId.value)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByRecurringRuleId(recurringRuleId).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncomeBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllBetweenAndType(startDate, endDate, TransactionType.INCOME)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Income
                }
        }
    }

    override suspend fun findAllExpenseBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllBetweenAndType(startDate, endDate, TransactionType.EXPENSE)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Expense
                }
        }
    }

    override suspend fun findAllTransferBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllBetweenAndType(startDate, endDate, TransactionType.TRANSFER)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull() as? Transfer
                }
        }
    }

    override suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)
                .mapNotNull {
                    val accountAssetCode = getAssetCodeForAccount(it.accountId)
                    with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findById(id: TransactionId): Transaction? {
        return withContext(Dispatchers.IO) {
            dataSource.findById(id.value)?.let {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findByIsSyncedAndIsDeleted(synced, deleted).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countHappenedTransactions(): Long {
        return withContext(Dispatchers.IO) {
            dataSource.countHappenedTransactions()
        }
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByTitleMatchingPattern(pattern).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        return withContext(Dispatchers.IO) {
            dataSource.countByTitleMatchingPattern(pattern)
        }
    }

    override suspend fun findAllByCategory(categoryId: CategoryId): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByCategory(categoryId.value).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: CategoryId
    ): Long {
        return withContext(Dispatchers.IO) {
            dataSource.countByTitleMatchingPatternAndCategoryId(pattern, categoryId.value)
        }
    }

    override suspend fun findAllByAccount(accountId: AccountId): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByAccount(accountId.value).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: AccountId
    ): Long {
        return withContext(Dispatchers.IO) {
            dataSource.countByTitleMatchingPatternAndAccountId(pattern, accountId.value)
        }
    }

    override suspend fun findLoanTransaction(loanId: UUID): Transaction? {
        return withContext(Dispatchers.IO) {
            dataSource.findLoanTransaction(loanId)?.let {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): Transaction? {
        return withContext(Dispatchers.IO) {
            dataSource.findLoanRecordTransaction(loanRecordId)?.let {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<Transaction> {
        return withContext(Dispatchers.IO) {
            dataSource.findAllByLoanId(loanId).mapNotNull {
                val accountAssetCode = getAssetCodeForAccount(it.accountId)
                with(mapper) { it.toDomain(accountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun save(accountId: AccountId, value: Transaction) {
        withContext(Dispatchers.IO) {
            dataSource.save(
                with(mapper) { value.toEntity(accountId) }
            )
        }
    }

    override suspend fun saveMany(
        accountId: AccountId,
        value: List<Transaction>
    ) {
        withContext(Dispatchers.IO) {
            dataSource.saveMany(
                value.map { with(mapper) { it.toEntity(accountId) } }
            )
        }
    }

    override suspend fun flagDeleted(id: TransactionId) {
        withContext(Dispatchers.IO) {
            dataSource.flagDeleted(id.value)
        }
    }

    override suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        withContext(Dispatchers.IO) {
            dataSource.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    override suspend fun flagDeletedByAccountId(accountId: AccountId) {
        withContext(Dispatchers.IO) {
            dataSource.flagDeletedByAccountId(accountId.value)
        }
    }

    override suspend fun deleteById(id: TransactionId) {
        withContext(Dispatchers.IO) {
            dataSource.deleteById(id.value)
        }
    }

    override suspend fun deleteAllByAccountId(accountId: AccountId) {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllByAccountId(accountId.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAll()
        }
    }

    private suspend fun getAssetCodeForAccount(accountId: UUID): AssetCode? {

        return accountRepository.findById(AccountId(accountId))?.asset
    }
}