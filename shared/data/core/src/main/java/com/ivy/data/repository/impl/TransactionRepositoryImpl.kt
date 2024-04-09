package com.ivy.data.repository.impl

import com.ivy.base.model.TransactionType
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.TagId
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@Suppress("LargeClass")
class TransactionRepositoryImpl @Inject constructor(
    private val mapper: TransactionMapper,
    private val transactionDao: TransactionDao,
    private val writeTransactionDao: WriteTransactionDao,
    private val dispatchersProvider: DispatchersProvider,
    private val tagRepository: TagsRepository
) : TransactionRepository {
    override suspend fun findAll(): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            val tagMap = async { findAllTagAssociations() }
            transactionDao.findAll().mapNotNull {
                val tags = tagMap.await()[it.id] ?: emptyList()
                with(mapper) { it.toDomain(tags = tags) }.getOrNull()
            }
        }
    }

    override suspend fun findAllIncomeByAccount(accountId: AccountId): List<Income> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.INCOME, accountId.value)
                .mapNotNull {
                    with(mapper) { it.toDomain() }.getOrNull() as? Income
                }
        }
    }

    override suspend fun findAllExpenseByAccount(accountId: AccountId): List<Expense> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.EXPENSE, accountId.value)
                .mapNotNull {
                    with(mapper) { it.toDomain() }.getOrNull() as? Expense
                }
        }
    }

    override suspend fun findAllTransferByAccount(accountId: AccountId): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllByTypeAndAccount(TransactionType.TRANSFER, accountId.value)
                .mapNotNull {
                    with(mapper) { it.toDomain() }.getOrNull() as? Transfer
                }
        }
    }

    override suspend fun findAllTransfersToAccount(
        toAccountId: AccountId
    ): List<Transfer> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllTransfersToAccount(toAccountId.value).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull() as? Transfer
            }
        }
    }


    override suspend fun findAllBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            val transactions = transactionDao.findAllBetween(startDate, endDate)
            val tagAssociationMap = getTagsForTransactionIds(transactions)

            transactions.mapNotNull {
                val tags = tagAssociationMap[it.id] ?: emptyList()

                with(mapper) { it.toDomain(tags = tags) }.getOrNull()
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
                    with(mapper) { it.toDomain() }.getOrNull()
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
                    with(mapper) { it.toDomain() }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetween(startDate, endDate).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
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
                    with(mapper) { it.toDomain() }.getOrNull()
                }
        }
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            transactionDao.findAllDueToBetweenByCategoryUnspecified(startDate, endDate).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
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
                    with(mapper) { it.toDomain() }.getOrNull()
                }
        }
    }

    override suspend fun findById(id: TransactionId): Transaction? {
        return withContext(dispatchersProvider.io) {
            transactionDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findByIds(ids: List<TransactionId>): List<Transaction> {
        return withContext(dispatchersProvider.io) {
            val tagMap = async { findTagsForTransactionIds(ids) }
            transactionDao.findByIds(ids.map { it.value }).mapNotNull {
                val tags = tagMap.await()[it.id] ?: emptyList()
                with(mapper) { it.toDomain(tags = tags) }.getOrNull()
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

    private suspend fun getTagsForTransactionIds(
        transactions: List<TransactionEntity>
    ): Map<UUID, List<TagId>> {
        return findTagsForTransactionIds(transactions.map { TransactionId(it.id) })
    }

    private suspend fun findTagsForTransactionIds(
        transactionIds: List<TransactionId>
    ): Map<UUID, List<TagId>> {
        return tagRepository.findByAssociatedId(transactionIds.map { AssociationId(it.value) })
            .entries.associate {
                it.key.value to it.value.map { ta -> ta.id }
            }
    }

    private suspend fun findAllTagAssociations(): Map<UUID, List<TagId>> {
        return tagRepository.findByAllTagsForAssociations().entries.associate {
            it.key.value to it.value.map { ta -> ta.id }
        }
    }
}