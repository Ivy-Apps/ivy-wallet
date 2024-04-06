package com.ivy.data.repository.impl

import com.ivy.base.di.AppCoroutineScope
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.DeleteOperation
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val mapper: AccountMapper,
    private val accountDao: AccountDao,
    private val writeAccountDao: WriteAccountDao,
    private val dispatchersProvider: DispatchersProvider,
    private val dataObserver: DataObserver,
    @AppCoroutineScope
    private val appCoroutineScope: CoroutineScope,
) : AccountRepository {

    init {
        appCoroutineScope.launch {
            dataObserver.writeEvents.collectLatest { event ->
                when (event) {
                    DataWriteEvent.AllDataChange -> accountsMemo.clear()
                    is DataWriteEvent.DeleteAccounts -> {
                        when (val op = event.operation) {
                            DeleteOperation.All -> accountsMemo.clear()
                            is DeleteOperation.Just -> {
                                op.ids.forEach(accountsMemo::remove)
                            }
                        }
                    }

                    is DataWriteEvent.SaveAccounts -> {
                        event.accounts.map(Account::id)
                            .forEach(accountsMemo::remove)
                    }

                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private val accountsMemo = mutableMapOf<AccountId, Account>()

    override suspend fun findById(id: AccountId): Account? {
        return accountsMemo[id] ?: withContext(dispatchersProvider.io) {
            accountDao.findById(id.value)?.let {
                with(mapper) { it.toDomain() }.getOrNull()
            }.also {
                if (it != null) {
                    accountsMemo[id] = it
                }
            }
        }
    }

    override suspend fun findAll(deleted: Boolean): List<Account> {
        return if (accountsMemo.isNotEmpty()) {
            accountsMemo.values.sortedBy { it.orderNum }
        } else {
            withContext(dispatchersProvider.io) {
                accountDao.findAll(deleted).mapNotNull {
                    with(mapper) { it.toDomain() }.getOrNull()
                }.also(::memoize)
            }
        }
    }

    override suspend fun findMaxOrderNum(): Double {
        return if (accountsMemo.isNotEmpty()) {
            accountsMemo.maxOfOrNull { (_, acc) -> acc.orderNum } ?: 0.0
        } else {
            withContext(dispatchersProvider.io) {
                accountDao.findMaxOrderNum() ?: 0.0
            }
        }
    }

    override suspend fun save(value: Account) {
        withContext(dispatchersProvider.io) {
            writeAccountDao.save(
                with(mapper) { value.toEntity() }
            )
            // Memoize
            accountsMemo[value.id] = value
            dataObserver.post(DataWriteEvent.SaveAccounts(listOf(value)))
        }
    }

    override suspend fun saveMany(values: List<Account>) {
        withContext(dispatchersProvider.io) {
            writeAccountDao.saveMany(
                values.map { with(mapper) { it.toEntity() } }
            )
            memoize(values)
            dataObserver.post(DataWriteEvent.SaveAccounts(values))
        }
    }

    private fun memoize(accounts: List<Account>) {
        accounts.forEach {
            accountsMemo[it.id] = it
        }
    }

    override suspend fun deleteById(id: AccountId) {
        withContext(dispatchersProvider.io) {
            accountsMemo.remove(id)
            writeAccountDao.deleteById(id.value)
            dataObserver.post(
                DataWriteEvent.DeleteAccounts(DeleteOperation.Just(listOf(id)))
            )
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchersProvider.io) {
            accountsMemo.clear()
            writeAccountDao.deleteAll()
            dataObserver.post(DataWriteEvent.DeleteAccounts(DeleteOperation.All))
        }
    }
}
