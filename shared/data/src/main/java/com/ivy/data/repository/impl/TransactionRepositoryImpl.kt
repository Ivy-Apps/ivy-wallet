package com.ivy.data.repository.impl

import com.ivy.base.model.TransactionType
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
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
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("LargeClass")
@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val accountRepository: AccountRepository,
    private val mapper: TransactionMapper,
    private val transactionDao: TransactionDao,
    private val writeTransactionDao: WriteTransactionDao,
    private val dispatchersProvider: DispatchersProvider,
) : TransactionRepository {
    // TODO: [BUG] Accounts that are updated should be evicted from the cache
    private val accountIdToAssetCodeCache = mutableMapOf<UUID, AssetCode>()

    override suspend fun findAll(): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAll().mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAll_LIMIT_1(): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAll_LIMIT_1().mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncome(): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByType(TransactionType.INCOME).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpense(): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByType(TransactionType.EXPENSE).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransfer(): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByType(TransactionType.TRANSFER).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.INCOME, accountId.value)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Income
                }
        }
    }

    override suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.EXPENSE, accountId.value)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Expense
                }
        }
    }

    override suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.TRANSFER, accountId.value)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Transfer
                }
        }
    }

    override suspend fun findAllIncomeByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccountBetween(
                type = TransactionType.INCOME,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpenseByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccountBetween(
                type = TransactionType.EXPENSE,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransferByAccountBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccountBetween(
                type = TransactionType.TRANSFER,
                accountId = accountId.value,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllTransfersToAccount(
        toAccountId: AccountId
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllTransfersToAccount(toAccountId.value).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllTransfersToAccountBetween(
                toAccountId = toAccountId.value,
                startDate = startDate,
                endDate = endDate,
                type = TransactionType.TRANSFER
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllBetween(startDate, endDate).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllByAccountAndBetween(
        accountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByAccountAndBetween(accountId.value, startDate, endDate)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByCategoryAndBetween(categoryId.value, startDate, endDate)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllUnspecifiedAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllUnspecifiedAndBetween(startDate, endDate).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncomeByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.INCOME,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllExpenseByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.EXPENSE,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllTransferByCategoryAndBetween(
        categoryId: CategoryId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByCategoryAndTypeAndBetween(
                categoryId = categoryId.value,
                type = TransactionType.TRANSFER,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllUnspecifiedIncomeAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.INCOME,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Income
            }
        }
    }

    override suspend fun findAllUnspecifiedExpenseAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.EXPENSE,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Expense
            }
        }
    }

    override suspend fun findAllUnspecifiedTransferAndBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllUnspecifiedAndTypeAndBetween(
                type = TransactionType.TRANSFER,
                startDate = startDate,
                endDate = endDate
            ).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) {
                    it.toDomain(
                        accountAssetCode,
                        toAccountAssetCode
                    )
                }.getOrNull() as? Transfer
            }
        }
    }

    override suspend fun findAllToAccountAndBetween(
        toAccountId: AccountId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllToAccountAndBetween(toAccountId.value, startDate, endDate)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetween(startDate, endDate).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllDueToBetweenByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        categoryId: CategoryId
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetweenByCategory(startDate, endDate, categoryId.value)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetweenByCategoryUnspecified(startDate, endDate).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllDueToBetweenByAccount(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        accountId: AccountId
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetweenByAccount(startDate, endDate, accountId.value)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByRecurringRuleId(recurringRuleId).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncomeBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllBetweenAndType(startDate, endDate, TransactionType.INCOME)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Income
                }
        }
    }

    override suspend fun findAllExpenseBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllBetweenAndType(startDate, endDate, TransactionType.EXPENSE)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Expense
                }
        }
    }

    override suspend fun findAllTransferBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllBetweenAndType(startDate, endDate, TransactionType.TRANSFER)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) {
                        it.toDomain(
                            accountAssetCode,
                            toAccountAssetCode
                        )
                    }.getOrNull() as? Transfer
                }
        }
    }

    override suspend fun findAllBetweenAndRecurringRuleId(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        recurringRuleId: UUID
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)
                .mapNotNull {
                    val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                        it.accountId,
                        it.toAccountId
                    )
                    with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
                }
        }
    }

    override suspend fun findById(id: TransactionId): Transaction? {
        return withContext(dispatchersProvider.io) {
            transactionDao.findById(id.value)?.let {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findByIsSyncedAndIsDeleted(synced, deleted).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countHappenedTransactions(): Long {
        return withContext(dispatchersProvider.io) {
            transactionDao.countHappenedTransactions()
        }
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTitleMatchingPattern(pattern).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        return withContext(dispatchersProvider.io) {
            transactionDao.countByTitleMatchingPattern(pattern)
        }
    }

    override suspend fun findAllByCategory(categoryId: CategoryId): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByCategory(categoryId.value).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(
        pattern: String,
        categoryId: CategoryId
    ): Long {
        return withContext(dispatchersProvider.io) {
            transactionDao.countByTitleMatchingPatternAndCategoryId(pattern, categoryId.value)
        }
    }

    override suspend fun findAllByAccount(accountId: AccountId): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByAccount(accountId.value).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(
        pattern: String,
        accountId: AccountId
    ): Long {
        return withContext(dispatchersProvider.io) {
            transactionDao.countByTitleMatchingPatternAndAccountId(pattern, accountId.value)
        }
    }

    override suspend fun findLoanTransaction(loanId: UUID): Transaction? {
        return withContext(dispatchersProvider.io) {
            transactionDao.findLoanTransaction(loanId)?.let {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): Transaction? {
        return withContext(dispatchersProvider.io) {
            transactionDao.findLoanRecordTransaction(loanRecordId)?.let {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByLoanId(loanId).mapNotNull {
                val (accountAssetCode, toAccountAssetCode) = getAssetCodes(
                    it.accountId,
                    it.toAccountId
                )
                with(mapper) { it.toDomain(accountAssetCode, toAccountAssetCode) }.getOrNull()
            }
        }
    }

    override suspend fun save(accountId: AccountId, value: Transaction) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.save(
                with(mapper) { value.toEntity() }
            )
        }
    }

    override suspend fun saveMany(
        accountId: AccountId,
        value: List<Transaction>
    ) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.saveMany(
                value.map { with(mapper) { it.toEntity() } }
            )
        }
    }

    override suspend fun flagDeleted(id: TransactionId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.flagDeleted(id.value)
        }
    }

    override suspend fun flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    override suspend fun flagDeletedByAccountId(accountId: AccountId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.flagDeletedByAccountId(accountId.value)
        }
    }

    override suspend fun deleteById(id: TransactionId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteById(id.value)
        }
    }

    override suspend fun deleteAllByAccountId(accountId: AccountId) {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteAllByAccountId(accountId.value)
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            writeTransactionDao.deleteAll()
        }
    }

    private suspend fun getAssetCodes(
        accountId: UUID,
        toAccountId: UUID?
    ): Pair<AssetCode?, AssetCode?> {
        val assetCode = getAssetCodeForAccount(accountId)
        val toAssetCode = toAccountId?.let { getAssetCodeForAccount(it) }
        return Pair(assetCode, toAssetCode)
    }

    private suspend fun getAssetCodeForAccount(accountId: UUID): AssetCode? {
        return accountIdToAssetCodeCache[accountId]
            ?: accountRepository.findById(AccountId(accountId))?.asset?.also {
                accountIdToAssetCodeCache[accountId] = it
            }
    }
}