package com.ivy.data.repository.impl

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataWriteEvent
import com.ivy.data.DataWriteEventBus
import com.ivy.data.DeleteOperation
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val mapper: AccountMapper,
    private val accountDao: AccountDao,
    private val writeAccountDao: WriteAccountDao,
    private val dispatchersProvider: DispatchersProvider,
    private val writeEventBus: DataWriteEventBus,
) : AccountRepository {
    override suspend fun findById(id: AccountId): Account? {
        return withContext(dispatchersProvider.io) {
            accountDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findAll(deleted: Boolean): List<Account> {
        return withContext(dispatchersProvider.io) {
            accountDao.findAll(deleted).mapNotNull {
                with(mapper) { it.toDomain() }.getOrNull()
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return withContext(dispatchersProvider.io) {
            accountDao.findMaxOrderNum() ?: 0.0
        }
    }

    override suspend fun save(value: Account) {
        withContext(dispatchersProvider.io) {
            writeAccountDao.save(
                with(mapper) { value.toEntity() }
            )
            writeEventBus.post(DataWriteEvent.SaveAccounts(listOf(value)))
        }
    }

    override suspend fun saveMany(values: List<Account>) {
        withContext(dispatchersProvider.io) {
            writeAccountDao.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
            writeEventBus.post(DataWriteEvent.SaveAccounts(values))
        }
    }

    override suspend fun deleteById(id: AccountId) {
        withContext(dispatchersProvider.io) {
            writeAccountDao.deleteById(id.value)
            writeEventBus.post(
                DataWriteEvent.DeleteAccounts(DeleteOperation.Just(listOf(id)))
            )
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            writeAccountDao.deleteAll()
            writeEventBus.post(DataWriteEvent.DeleteAccounts(DeleteOperation.All))
        }
    }
}
