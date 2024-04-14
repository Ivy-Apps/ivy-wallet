package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataWriteEvent
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.RepositoryMemoFactory
import com.ivy.data.repository.mapper.AccountMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val mapper: AccountMapper,
    private val accountDao: AccountDao,
    private val writeAccountDao: WriteAccountDao,
    private val dispatchersProvider: DispatchersProvider,
    memoFactory: RepositoryMemoFactory,
) : AccountRepository {

    private val memo = memoFactory.createMemo(
        getDataWriteSaveEvent = DataWriteEvent::SaveAccounts,
        getDateWriteDeleteEvent = DataWriteEvent::DeleteAccounts
    )

    override suspend fun findById(id: AccountId): Account? = memo.findById(
        id = id,
        findByIdOperation = {
            accountDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    )

    override suspend fun findAll(deleted: Boolean): List<Account> = memo.findAll(
        findAllOperation = {
            accountDao.findAll(deleted).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        },
        sortMemo = { sortedBy(Account::orderNum) }
    )

    override suspend fun findMaxOrderNum(): Double = if (memo.findAllMemoized) {
        memo.items.maxOfOrNull { (_, acc) -> acc.orderNum } ?: 0.0
    } else {
        withContext(dispatchersProvider.io) {
            accountDao.findMaxOrderNum() ?: 0.0
        }
    }

    override suspend fun save(value: Account): Unit = memo.save(value) {
        writeAccountDao.save(
            with(mapper) { it.toEntity() }
        )
    }

    override suspend fun saveMany(values: List<Account>): Unit = memo.saveMany(values) {
        writeAccountDao.saveMany(
            it.map { with(mapper) { it.toEntity() } }
        )
    }

    override suspend fun deleteById(id: AccountId): Unit = memo.deleteById(id) {
        writeAccountDao.deleteById(id.value)
    }

    override suspend fun deleteAll(): Unit = memo.deleteAll(
        deleteAllOperation = writeAccountDao::deleteAll
    )
}
